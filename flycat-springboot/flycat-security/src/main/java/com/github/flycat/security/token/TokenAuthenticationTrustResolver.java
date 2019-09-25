package com.github.flycat.security.token;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

public class TokenAuthenticationTrustResolver extends AuthenticationTrustResolverImpl implements AuthenticationTrustResolver {

    @Override
    public boolean isAnonymous(Authentication authentication) {
        if (authentication instanceof TokenAuthentication) {
            TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
            return !(tokenAuthentication.isValid());
        }
        return super.isAnonymous(authentication);
    }

    @Override
    public boolean isRememberMe(Authentication authentication) {
        return super.isRememberMe(authentication);
    }
}
