package com.kraken.security.authentication.client.api;

import com.kraken.security.authentication.api.AuthenticationMode;

public interface AuthenticatedClientFactory<T extends AuthenticatedClient> {

    T create();

    T create(AuthenticationMode mode);

    T create(AuthenticationMode mode, String applicationId);

    T create(AuthenticationMode mode, String applicationId, String userId);
}
