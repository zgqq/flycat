package com.github.bootbox.server.log;

import com.google.common.collect.Sets;

import java.util.Set;

public final class MDCUtils {


    public static final String LOG_MDC = "logMDC";

    public static final String HTTP_URI = "req.requestURI";
    public static final String HTTP_METHOD = "req.method";
    public static final String HTTP_AGENT = "req.userAgent";

    public static final Set<String> MDC_CONSTANTS = Sets.newHashSet(HTTP_URI,
            HTTP_METHOD);

}
