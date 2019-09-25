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
package com.github.flycat.security.token;

import lombok.Data;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class TokenAuthentication extends AbstractAuthenticationToken {
    private final Object principal;
    private final Object credentials;
    private final boolean valid;

    public TokenAuthentication(Object principal, Object credentials) {
        this(principal, credentials, null, false);
    }

    public TokenAuthentication(Object principal, Object credentials,
                               Collection<? extends GrantedAuthority> authorities,
                               boolean valid) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.valid = valid;
        setAuthenticated(this.valid);
    }
}
