package com.github.flycat.web.spring;

import com.github.flycat.context.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ServerStatus {

    private static final Logger logger = LoggerFactory.getLogger(ServerStatus.class);

    public static ResponseEntity response() {
        return response(null);
    }

    public static ResponseEntity response(StatusChecker runnable) {
        boolean b = ContextUtils.serverRunning();
        ResponseEntity responseEntity;
        if (b) {
            try {
                if (runnable != null) {
                    runnable.run();
                }
                responseEntity = new ResponseEntity("ok",
                        HttpStatus.OK
                );
            } catch (Exception e) {
                logger.error("Status error! ", e);
                responseEntity = new ResponseEntity("Server error",
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
        } else {
            responseEntity = new ResponseEntity<>("Server stop",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
        return responseEntity;
    }
}
