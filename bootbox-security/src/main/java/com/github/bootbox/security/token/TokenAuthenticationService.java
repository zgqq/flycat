package com.github.bootbox.security.token;

import javax.servlet.http.HttpServletRequest;

public interface TokenAuthenticationService {
    TokenInformation authenticateToken(HttpServletRequest httpServletRequest);
}
