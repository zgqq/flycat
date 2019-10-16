package com.github.flycat.platform.springboot.web;

import com.github.flycat.util.StringUtils;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public abstract class AbstractSmoothWebServerCustomizer implements
        ApplicationListener<ServletWebServerInitializedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSmoothWebServerCustomizer.class);

    private volatile int serverPort;
    private final boolean tryKillProcess;
    private static volatile WebServer webServer;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (webServer != null) {
                    LOGGER.info("Closing web server");
                    webServer.stop();
                }
            }
        });
    }

    public AbstractSmoothWebServerCustomizer(boolean tryKillProcess) {
        this.tryKillProcess = tryKillProcess;
    }

    public static String getPid(int port) {
        final Stopwatch started = Stopwatch.createStarted();
        try {
            if (isWindow()) {
                ProcessBuilder pb = new ProcessBuilder("CMD", "/C", "netstat -a -n -o | find \"" + port + "\" | " +
                        " find LISTEN"
                );
                Process pr = pb.start();
                pr.waitFor();
                if (pr.exitValue() == 0) {
                    BufferedReader outReader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                    final String pidLine = outReader.readLine().trim();
                    System.out.println("Got pid line " + pidLine);
                    if (StringUtils.isNotBlank(pidLine)) {
                        final String[] pidArr = pidLine.split("\\s+");
                        return pidArr[pidArr.length - 1];
                    }
                } else {
                    BufferedReader outReader = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
                    final String error = outReader.readLine();
                    System.out.println("Error while getting PID " + error);
                }
            } else {
                ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", "lsof -i:" + port + " | grep LISTEN");
                Process pr = pb.start();
                pr.waitFor();
                if (pr.exitValue() == 0) {
                    BufferedReader outReader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                    final String pidLine = outReader.readLine().trim();
                    System.out.println("Got pid line " + pidLine);
                    if (StringUtils.isNotBlank(pidLine)) {
                        final String[] pidArr = pidLine.split("\\s+");
                        return pidArr[1];
                    }
                } else {
                    BufferedReader outReader = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
                    final String error = outReader.readLine();
                    System.out.println("Error while getting PID " + error);
                }
            }
            return "";
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            started.stop();
            LOGGER.info("Got pid, port:{}, cost:{}", port, started);
        }
    }


    public static void kill(Integer pid) {
        LOGGER.info("Kill process, pid:{}", pid);
        ProcessBuilder pb;
        if (isWindow()) {
            pb = new ProcessBuilder("CMD", "/C", "taskkill /F /PID " + pid);
        } else {
            pb = new ProcessBuilder("/bin/sh", "-c", "kill " + pid);
        }

        Process pr = null;
        try {
            pr = pb.start();
            pr.waitFor();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isWindow() {
        final String property = System.getProperty("os.name");
        return property != null && property.startsWith("Windows");
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        try {
            webServer = event.getWebServer();
            if (!tryKillProcess) {
                return;
            }
            final String pid = getPid(serverPort);
            LOGGER.info("Changing server port, port:{}, previous pid:{}, needChange:{}", serverPort, pid, tryKillProcess);
            stopConnector();
            if (StringUtils.isNotBlank(pid)) {
                final Stopwatch started = Stopwatch.createStarted();
                kill(Integer.valueOf(pid));
                started.stop();
                LOGGER.info("Killed process, cost:{}", started);
                started.start();
                retryStartConnector();
                started.stop();
                LOGGER.info("Closed previous port, cost:{}", started);
            } else {
                startConnector();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTryKillProcess() {
        return tryKillProcess;
    }

    protected abstract void retryStartConnector() throws Throwable;

    protected abstract boolean startConnector() throws Throwable;

    protected abstract void stopConnector() throws Throwable;
}