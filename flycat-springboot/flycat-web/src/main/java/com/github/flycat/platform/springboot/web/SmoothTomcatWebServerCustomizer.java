package com.github.flycat.platform.springboot.web;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

public class SmoothTomcatWebServerCustomizer extends AbstractSmoothWebServerCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmoothTomcatWebServerCustomizer.class);

    private volatile Connector mainConnector;
    private volatile int serverPort;

    public SmoothTomcatWebServerCustomizer(boolean tryKillProcess) {
        super(tryKillProcess);
    }

    @Override
    protected void retryStartConnector() throws Throwable {
        for (; ; ) {
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
