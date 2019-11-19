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
package com.github.flycat.platform.springboot.web;

import com.github.flycat.web.WebException;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

import java.util.function.Consumer;

/**
 * unbind endpoint 2000 ms
 * close server 2000 ms
 * close handler 50 ms
 * pause handler 2000 ms
 */
public class SmoothTomcatWebServerCustomizer extends AbstractSmoothWebServerCustomizer
        implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmoothTomcatWebServerCustomizer.class);
    public static final int MAX_TRY_COUNT = 9999;

    private volatile Connector mainConnector;
    private volatile int serverPort;

    public SmoothTomcatWebServerCustomizer(boolean killAfterStarted) {
        super(killAfterStarted);
    }

    @Override
    protected void addShutdownHook() {
        addHook(new Consumer<WebServer>() {
            @Override
            public void accept(WebServer webServer) {
                closeServerSocket((TomcatWebServer) webServer);
            }
        });
    }

    private void closeServerSocket(TomcatWebServer webServer) {
        try {
            LOGGER.info("Closing server socket");
            mainConnector.getProtocolHandler().closeServerSocketGraceful();
            LOGGER.info("Closed server socket");
        } catch (Exception e) {
            LOGGER.error("Unable to close handler", e);
        }
    }

    private void pauseHandler(TomcatWebServer webServer) {
        AbstractProtocol handler = getProtocolHandler(webServer);
        try {
            LOGGER.info("Pausing handler");
            handler.pause();
            LOGGER.info("Paused handler");
        } catch (Exception e) {
            LOGGER.error("Unable to close handler", e);
        }
    }

    private void stopHandler(TomcatWebServer webServer) {
        AbstractProtocol handler = getProtocolHandler(webServer);
        try {
            LOGGER.info("Closing handler");
            handler.stop();
            LOGGER.info("Closed handler");
        } catch (Exception e) {
            LOGGER.error("Unable to close handler", e);
        }
    }

    private AbstractProtocol getProtocolHandler(TomcatWebServer webServer) {
        Connector connector = webServer.getTomcat().getConnector();
        ProtocolHandler protocolHandler = connector.getProtocolHandler();
        if (protocolHandler instanceof AbstractProtocol) {
            AbstractProtocol handler = (AbstractProtocol) protocolHandler;
            return handler;
        }
        return null;
    }

    @Override
    protected void retryStartConnector() throws Throwable {

        Throwable throwable = null;
        for (int i = 0; i < MAX_TRY_COUNT; i++) {
            LOGGER.info("Trying start connector");
            try {
                final boolean success = startConnector();
                if (success) {
                    return;
                }
            } catch (Throwable e) {
                LOGGER.warn("Start connector exception, cause:{}", e.getMessage());
                throwable = e;
            }
            Thread.sleep(5L);
        }
        throw new WebException("Unable to start connector", throwable);
    }

    @Override
    protected boolean startConnector() throws Throwable {
        this.mainConnector.setPort(this.serverPort);
        this.mainConnector.start();
        return (this.mainConnector.getState() == LifecycleState.STARTED);
    }

    @Override
    protected void stopConnector() throws LifecycleException {
        this.mainConnector.stop();
    }

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        this.serverPort = factory.getPort();
        setServerPort(this.serverPort);
        factory.addConnectorCustomizers(connector -> {
            mainConnector = connector;
            if (isTryKillProcess()) {
                connector.setPort(0);
            }
        });
    }
}
