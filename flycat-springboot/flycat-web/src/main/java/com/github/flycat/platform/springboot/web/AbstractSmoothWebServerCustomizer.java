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

import com.github.flycat.util.StringUtils;
import com.github.flycat.util.reflect.FieldUtils;
import com.github.flycat.web.WebException;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public abstract class AbstractSmoothWebServerCustomizer implements
        ApplicationListener<ServletWebServerInitializedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSmoothWebServerCustomizer.class);

    private volatile int serverPort;
    private final boolean tryKillProcess;
    protected volatile WebServer webServer;
    protected Object monitor;
    protected Future<String> getPidTask;

    public AbstractSmoothWebServerCustomizer(boolean tryKillProcess) {
        this.tryKillProcess = tryKillProcess;
        addShutdownHook();
    }

    protected void addShutdownHook() {
        this.addHook(null);
    }

    public void addHook(Consumer<WebServer> beforeStop) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (monitor != null) {
                    synchronized (monitor) {
                        if (webServer != null) {
                            LOGGER.info("Closing web server");
                            if (beforeStop != null) {
                                beforeStop.accept(webServer);
                            }
                            webServer.stop();
                            LOGGER.info("Closed web server");
                        }
                    }
                }
            }
        });
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
        if (tryKillProcess) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            getPidTask = executorService.submit(() -> getPid(serverPort));
        }
    }

    @Override
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        try {
            webServer = event.getWebServer();
            monitor = FieldUtils.readDeclaredField(webServer, "monitor", true);
            beforeStart(webServer);
            if (!tryKillProcess || getPidTask == null) {
                return;
            }
            final String pid = getPidTask.get();
            LOGGER.info("Changing server port, port:{}, previous pid:{}, needChange:{}", serverPort, pid, tryKillProcess);
            try {
                stopConnector();
            } catch (Throwable throwable) {
            }
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
            throw new WebException(e);
        }
    }

    public boolean isTryKillProcess() {
        return tryKillProcess;
    }

    protected abstract void retryStartConnector() throws Throwable;

    protected abstract boolean startConnector() throws Throwable;

    protected abstract void stopConnector() throws Throwable;

    protected void beforeStart(WebServer webServer) throws Throwable {
    }
}
