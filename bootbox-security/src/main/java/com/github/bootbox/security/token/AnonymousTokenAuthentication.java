package com.github.bootbox.security.token;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AnonymousTokenAuthentication extends AnonymousAuthenticationToken  {
    public AnonymousTokenAuthentication(String key, Object principal,
                                        Collection<? extends GrantedAuthority> authorities) {
        super(key, principal, authorities);
    }
}
