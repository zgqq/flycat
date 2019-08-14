package com.github.bootbox.web.api;

public class ApiResponseCodeUtils {

    public static int getSystemErrorCode(int code) {
        final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
        final int systemErrorPlaceholderCode = apiFactory.getSystemErrorPlaceholderCode();
        return getApiResponseCode(systemErrorPlaceholderCode, code);
    }

    public static int getBusinessErrorCode(int code) {
        final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
        final int businessErrorPlaceholderCode = apiFactory.getBusinessErrorPlaceholderCode();
        return getApiResponseCode(businessErrorPlaceholderCode, code);
    }

    private static int getApiResponseCode(int levelCode, int code) {
        final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
        final int modulePlaceholderCode = apiFactory.getModulePlaceholderCode();
        final StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(levelCode);
        if (modulePlaceholderCode < 10) {
            codeBuilder.append("0");
        }
        codeBuilder.append(modulePlaceholderCode);

        if (code < 10) {
            codeBuilder.append("00");
        } else if (code < 100) {
            codeBuilder.append("0");
        }
        codeBuilder.append(code);
        return Integer.parseInt(codeBuilder.toString());
    }

    public static Object getUnknownExceptionResult(Throwable e) {
        final ApiFactory apiFactory = ApiFactoryHolder.getApiFactory();
        Object unknownExceptionResult = apiFactory.createUnknownExceptionResult(e);
        if (unknownExceptionResult == null) {
            unknownExceptionResult = apiFactory.createApiResult(ApiResponseCodeUtils
                    .getSystemErrorCode(ApiResponseCode.SERVER_UNKNOWN_ERROR), "服务器傲娇了!");

        }
        return unknownExceptionResult;
    }
}
