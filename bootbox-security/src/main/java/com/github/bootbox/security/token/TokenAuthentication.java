package com.github.bootbox.security.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class TokenAuthentication extends AbstractAuthenticationToken {
    private final Object principal;
    private final Object credentials;
    private final boolean valid;

    public TokenAuthentication(Object principal, Object credentials,
                               Collection<? extends GrantedAuthority> authorities,
                               boolean valid) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.valid = valid;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }
}
