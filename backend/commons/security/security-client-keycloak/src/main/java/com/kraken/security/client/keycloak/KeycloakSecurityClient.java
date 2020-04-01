package com.kraken.security.client.keycloak;

import com.kraken.security.client.api.SecurityClient;
import reactor.core.publisher.Mono;

public class KeycloakSecurityClient implements SecurityClient {
  public Mono<String> exchangeToken() {
    // TODO exchange a kraken-web token for a kraken-api one => see readme
    return null;
  }

  public Mono<String> refreshToken() {
    // TODO refresh the given token  
    return null;
  }
}
