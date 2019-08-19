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

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

public class SmoothTomcatWebServerCustomizer extends AbstractSmoothWebServerCustomizer
        implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmoothTomcatWebServerCustomizer.class);

    private volatile Connector mainConnector;
    private volatile int serverPort;

    public SmoothTomcatWebServerCustomizer(boolean tryKillProcess) {
        super(tryKillProcess);
    }

    @Override
    protected void retryStartConnector() throws Throwable {
        for (; ;) {
            LOGGER.info("Trying start connector");
            final boolean success = startConnector();
            if (success) {
                break;
            } else {
                stopConnector();
            }
        }
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
