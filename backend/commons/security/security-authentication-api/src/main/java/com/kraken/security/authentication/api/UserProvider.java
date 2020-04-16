package com.kraken.security.authentication.api;

import com.kraken.security.entity.owner.Owner;
import com.kraken.security.entity.owner.UserOwner;
import com.kraken.security.entity.user.KrakenUser;
import reactor.core.publisher.Mono;

public interface UserProvider {

  default Mono<Owner> getOwner(final String applicationId) {
    return this.getAuthenticatedUser().map(user -> UserOwner.builder()
        .applicationId(applicationId)
        .userId(user.getUserId())
        .roles(user.getRoles()).build());
  }

  Mono<KrakenUser> getAuthenticatedUser();

  Mono<String> getTokenValue();

}
