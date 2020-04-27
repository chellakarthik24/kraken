package com.kraken.keycloak.event.listener;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeycloakToken {

  String accessToken;
  Long expiresIn; // seconds

  @JsonCreator
  KeycloakToken(
      @JsonProperty("access_token") final String accessToken,
      @JsonProperty("expires_in") final Long expiresIn
  ) {
    super();
    this.accessToken = accessToken;
    this.expiresIn = expiresIn;
  }

}