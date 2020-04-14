package com.kraken.security.authentication.noop;

import com.kraken.security.authentication.api.UserProvider;
import com.kraken.security.entity.user.KrakenUser;
import reactor.core.publisher.Mono;

class NoopUserProvider implements UserProvider {
  @Override
  public Mono<KrakenUser> getAuthenticatedUser() {
    return Mono.error(new IllegalAccessException("Noop authentication does not provide a user."));
  }

  @Override
  public Mono<String> getTokenValue() {
    return Mono.error(new IllegalAccessException("Noop authentication does not provide a token value."));
  }

}
