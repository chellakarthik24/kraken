package com.kraken.security.authentication.api;

import com.kraken.security.entity.user.KrakenUser;
import reactor.core.publisher.Mono;

public interface UserProvider {

  Mono<KrakenUser> getAuthenticatedUser();

  Mono<String> getTokenValue();

}
