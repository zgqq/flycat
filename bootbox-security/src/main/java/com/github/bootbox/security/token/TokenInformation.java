package com.github.bootbox.security.token;

public class TokenInformation {
    private final String userId;
    private final String token;
    private final boolean valid;

    public TokenInformation(String userId, String token, boolean valid) {
        this.userId = userId;
        this.token = token;
        this.valid = valid;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public boolean isValid() {
        return valid;
    }
}
