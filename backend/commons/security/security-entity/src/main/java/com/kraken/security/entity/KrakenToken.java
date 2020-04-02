package com.kraken.security.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Objects.requireNonNull;


@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class KrakenToken {

  String accessToken;
  Integer expiresIn;
  Integer refreshExpiresIn;
  String refreshToken;
  String tokenType;
  String sessionState;
  String scope;

//{
//    "access_token": "ezrez.zerez.zerzer",
//    "expires_in": 300,
//    "refresh_expires_in": 1800,
//    "refresh_token": "zre.ezrzer.zerzer",
//    "token_type": "bearer",
//    "not-before-policy": 0,
//    "session_state": "102cf714-8603-4baf-b253-bf031dc4775a",
//    "scope": "email profile"
//    }

  @JsonCreator
  KrakenToken(
      @JsonProperty("access_token") final String accessToken,
      @JsonProperty("expires_in") final Integer expiresIn,
      @JsonProperty("refresh_expires_in") final Integer refreshExpiresIn,
      @JsonProperty("refresh_token") final String refreshToken,
      @JsonProperty("token_type") final String tokenType,
      @JsonProperty("session_state") final String sessionState,
      @JsonProperty("scope") final String scope
  ) {
    super();
    this.accessToken = requireNonNull(accessToken);
    this.expiresIn = requireNonNull(expiresIn);
    this.refreshExpiresIn = requireNonNull(refreshExpiresIn);
    this.refreshToken = requireNonNull(refreshToken);
    this.tokenType = nullToEmpty(tokenType);
    this.sessionState = nullToEmpty(sessionState);
    this.scope = nullToEmpty(scope);
  }

}