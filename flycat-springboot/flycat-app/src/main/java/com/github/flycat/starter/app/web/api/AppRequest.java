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
package com.github.flycat.starter.app.web.api;

import com.github.flycat.spi.json.JsonUtils;
import com.github.flycat.util.StringUtils;
import com.github.flycat.util.bean.CommonParameters;

import java.util.Map;
import java.util.StringJoiner;

/**
 * Created by zgq on 17-3-27.
 */
public class AppRequest {
    private String app;
    private Integer channel;
    private Map data;
    private String model;
    private Integer netType;
    private Integer term;
    private String token;
    private Long ts;
    private String udid;
    private Integer uid;
    private String version;
    private String openId;

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }


    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Integer getNetType() {
        return netType;
    }

    public void setNetType(Integer netType) {
        this.netType = netType;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getTs() {
        return ts;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public <T> T resolveParameter(String name, Class<T> clazz) {
        return (T) data.get(name);
    }

//    public <T> List<T> resolveDataList(String name, Class<T> clazz) {
//        Object o = data.get(name);
//        if (o instanceof ArrayList) {
//            ArrayList arrayList = (ArrayList) o;
//            ArrayList<T> list = new ArrayList<>();
//            ParserConfig config = ParserConfig.getGlobalInstance();
//            for (Object item : arrayList) {
//                T classItem = (T) TypeUtils.cast(item, clazz, config);
//                list.add(classItem);
//            }
//            return list;
//        } else if (o instanceof JSONArray) {
//            JSONArray objects = (JSONArray) o;
//            return objects.toJavaList(clazz);
//        }
//        return new ArrayList<>();
//    }

    public <T> T resolveData(Class<T> clazz) {
        Map dataMap = getData();
        String json = JsonUtils.toJsonString(dataMap);
        T t = JsonUtils.parseObject(json, clazz);
        injectCommonParameters(t);
        return t;
    }

    <T> void injectCommonParameters(T t) {
        if (t instanceof CommonParameters) {
            final CommonParameters commonParameters = (CommonParameters) t;

            commonParameters.setOpApp(app);

            commonParameters.setOpChannel(channel);

            commonParameters.setOpTerm(term);

            commonParameters.setOpUdid(udid);

            commonParameters.setOpUid(uid);
        }
    }

//    public <T> T convertDataAndSetOpUid(Class<T> clazz) {
//        Map dataMap = getData();
//        String json = JSON.toJSONString(dataMap);
//        T t = JSON.parseObject(json, clazz);
//        injectCommonParameters(t);
//        return t;
//    }

    public Integer resolvePageNum() {
        return resolveParameter("pageNum", Integer.class);
    }

    public Integer resolvePageSize() {
        return resolveParameter("pageSize", Integer.class);
    }

    public DataWrapper dataWrapper() {
        return new DataWrapper(data);
    }

    public Integer getVersionAsInteger() {
        if (StringUtils.isBlank(version)) {
            return null;
        }
        return Integer.valueOf(version);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AppRequest.class.getSimpleName() + "[", "]")
                .add("app='" + app + "'")
                .add("channel=" + channel)
                .add("data=" + data)
                .add("model='" + model + "'")
                .add("nettype=" + netType)
                .add("term=" + term)
                .add("token='" + token + "'")
                .add("ts=" + ts)
                .add("udid='" + udid + "'")
                .add("uid=" + uid)
                .add("version='" + version + "'")
                .add("openid='" + openId + "'")
                .toString();
    }
}
