/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.util.http;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HttpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    private static CloseableHttpClient httpclient;
    public static final String IPHONE5_USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 7_0 like Mac OS X;"
            + " en-us) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53";

    static {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(500)
                .setSocketTimeout(500)
                .setConnectTimeout(500)
                .build();
        httpclient = createClient();
    }


    public static CloseableHttpClient createClient() {
        int timeout = 30000;
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .build();

        return HttpClients.custom()
                .setConnectionManager(createHttpClientConnectionManager(new ClientConfiguration()))
                .setMaxConnPerRoute(50)
                .setMaxConnTotal(500)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {
                    @Override
                    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                        long keepAliveDuration = super.getKeepAliveDuration(response, context);
                        if (keepAliveDuration > 0) {
                            return keepAliveDuration;
                        }
                        return 10 * 1000;
                    }
                })
                .setDefaultRequestConfig(requestConfig).build();
    }


    public static HttpClientConnectionManager createHttpClientConnectionManager(ClientConfiguration config) {
        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                    return true;
                }
            }).build();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register(Protocol.HTTP.toString(), PlainConnectionSocketFactory.getSocketFactory())
                .register(Protocol.HTTPS.toString(), sslSocketFactory)
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setDefaultMaxPerRoute(config.getMaxConnections());
        connectionManager.setMaxTotal(config.getMaxConnections());
        connectionManager.setValidateAfterInactivity(config.getValidateAfterInactivity());
        connectionManager.setDefaultSocketConfig(SocketConfig.custom().
                setSoTimeout(config.getSocketTimeout()).setTcpNoDelay(true).build());
        if (config.isUseReaper()) {
            IdleConnectionReaper.setIdleConnectionTime(config.getIdleConnectionTime());
            IdleConnectionReaper.registerConnectionManager(connectionManager);
        }
        return connectionManager;
    }



    public static String getOrFail(String url, String encoding)
            throws IOException, URISyntaxException {
        Pair<Integer, String> p = get(url, null, encoding);
        if (p.getL() != 200) {
            throw new IOException("response code not 200:" + p.getL()
                    + ", body:" + p.getR());
        }
        return p.getR();
    }

    public static String getOrFail(String url, Map<String, String> params,
                                   String encoding) throws IOException, URISyntaxException {
        Pair<Integer, String> p = get(url, params, encoding);
        if (p.getL() != 200) {
            throw new IOException("response code not 200:" + p.getL()
                    + ", body:" + p.getR());
        }
        return p.getR();
    }

    public static Pair<Integer, String> get(String url,
                                            Map<String, String> params, String encoding,
                                            Map<String, String> headers) throws IOException, URISyntaxException {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url required!");
        }
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                nvps.add(new BasicNameValuePair(e.getKey(), e.getValue()));
            }
        }
        URIBuilder builder = new URIBuilder(url);
        if (!nvps.isEmpty()) {
            builder.setParameters(nvps);
        }
        // httpPost.addHeader("Content-type", "text/json; charset=" + encoding);
        HttpGet httpGet = new HttpGet(builder.build());
        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                httpGet.setHeader(e.getKey(), e.getValue());
            }
        }
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            int statusCode = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();

//            Header cookieHeader = response.getFirstHeader("Set-Cookie");
//            if (headers != null && cookieHeader != null) {
//                headers.put(cookieHeader.getName(), cookieHeader.getValue());
//            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            entity.writeTo(baos);

            Header firstHeader = response.getFirstHeader("Content-Type");
            if (firstHeader != null) {
                String head = firstHeader.toString();
                if (head.contains("charset")) {
                    Pattern pattern = Pattern.compile("charset.*=(.*)");
                    Matcher matcher = pattern.matcher(head);
                    if (matcher.find()) {
//                        charset=gb2312
                        String charset = matcher.group(1);
                        System.out.println("Using " + charset + ", instead of " + encoding+", head "+head);
                        encoding  = charset;
                    }
                }
            }
            String responseBody = new String(baos.toByteArray(), encoding);

            EntityUtils.consume(entity);

            return new Pair<Integer, String>(statusCode, responseBody);
        } finally {
            response.close();
        }

    }

    public static void main(String[] args) throws IOException, URISyntaxException {

    }

    public static Pair<Integer, String> get(String url,
                                            Map<String, String> params, String encoding) throws IOException,
            URISyntaxException {
        return get(url, params, encoding, null);

    }

    public static Pair<Integer, String> post(String url,
                                             Map<String, String> params, String encoding) throws IOException {
        return post(url, params, encoding, null);
    }

    public static Pair<Integer, String> postWithString(String url,
                                                       String params,
                                                       String encoding,
                                                       int timeoutInMs) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(params, encoding));
        return postWithTimeout(encoding, ImmutableMap.of(), timeoutInMs, httpPost);

    }

    public static Pair<Integer, String> postWithTimeout(String url,
                                                        Map<String, String> params,
                                                        String encoding,
                                                        Map<String, String> headers,
                                                        int timeoutInMs) throws IOException {
        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                nvps.add(new BasicNameValuePair(e.getKey(), e.getValue()));
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));
        return postWithTimeout(encoding, headers, timeoutInMs, httpPost);


    }

    private static Pair<Integer, String> postWithTimeout(String encoding, Map<String, String> headers, int timeoutInMs,
                                                         HttpPost httpPost) throws IOException {
    /*
    StringBuilder sb = new StringBuilder();
    if (params != null && !params.isEmpty()) {
        for (Map.Entry<String, String> e : params.entrySet()) {
            sb.append(e.getKey()).append("=").append(e.getValue()).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
    }
    httpPost.setEntity(new StringEntity(sb.toString()));
    */
        // httpPost.addHeader("Content-type", "text/json; charset=" + encoding);
        if (headers == null || headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }

        if (timeoutInMs > 0) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(timeoutInMs)
                    .setSocketTimeout(timeoutInMs)
                    .setConnectTimeout(timeoutInMs)
                    .build();
            httpPost.setConfig(requestConfig);
        }

        CloseableHttpResponse response = httpclient.execute(httpPost);
        try {
            int statusCode = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            entity.writeTo(baos);

            String responseBody = new String(baos.toByteArray(), encoding);

            EntityUtils.consume(entity);

            return new Pair<Integer, String>(statusCode, responseBody);
        } finally {
            response.close();
        }
    }

    public static Pair<Integer, String> post(String url,
                                             Map<String, String> params,
                                             String encoding,
                                             Map<String, String> headers) throws IOException {
        return postWithTimeout(url, params, encoding, headers, -1);
    }

    public static String postOrFail(String url, String postBody, String encoding)
            throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent", "Mozilla/5.0");
        httpPost.setEntity(new StringEntity(postBody, encoding));
        CloseableHttpResponse response = httpclient.execute(httpPost);
        try {
            int statusCode = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            entity.writeTo(baos);

            String responseBody = new String(baos.toByteArray(), encoding);

            EntityUtils.consume(entity);

            if (statusCode != 200) {
                throw new IOException("response code not 200:" + statusCode
                        + ", body:" + responseBody);
            }
            return responseBody;
        } finally {
            response.close();
        }
    }

    public static String postOrFail(String url, String postBody,
                                    String encoding, int timeout) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent", "Mozilla/5.0");
        httpPost.setEntity(new StringEntity(postBody, encoding));
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeout).setSocketTimeout(timeout)
                .setConnectTimeout(timeout).build();
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = httpclient.execute(httpPost);
        try {
            int statusCode = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            entity.writeTo(baos);

            String responseBody = new String(baos.toByteArray(), encoding);

            EntityUtils.consume(entity);

            if (statusCode != 200) {
                throw new IOException("response code not 200:" + statusCode
                        + ", body:" + responseBody);
            }
            return responseBody;
        } finally {
            response.close();
        }
    }

    public static String postJson(String url, String json, String encoding)
            throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent", "Mozilla/5.0");
        httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
        httpPost.setEntity(new StringEntity(json, encoding));
        CloseableHttpResponse response = httpclient.execute(httpPost);
        try {
            int statusCode = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            entity.writeTo(baos);

            String responseBody = new String(baos.toByteArray(), encoding);

            EntityUtils.consume(entity);

            if (statusCode != 200) {
                throw new IOException("response code not 200:" + statusCode
                        + ", body:" + responseBody);
            }
            return responseBody;
        } finally {
            response.close();
        }
    }

    public static String get(String url) {
        try {
            return getOrFail(url, Charset.defaultCharset().name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public static String get(String url, Map<String, String> headers) {
        try {
            return get(url, null, Charset.defaultCharset().name(), headers).getR();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public static String postJson(String url, String json) throws IOException {
        return postJson(url, json, Charset.defaultCharset().name());
    }
}
