package com.kraken.security.authentication.api;

import com.kraken.security.entity.KrakenUser;
import reactor.core.publisher.Mono;

public interface AuthenticatedUserProvider {
  Mono<KrakenUser> getAuthenticatedUser();
  Mono<String> getTokenValue();
}
