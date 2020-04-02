package com.kraken.security.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import static java.util.Objects.requireNonNull;


@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class KrakenToken {

  String accessToken;
  String refreshToken;

  @JsonCreator
  KrakenToken(
      @JsonProperty("access_token") final String accessToken,
      @JsonProperty("refresh_token") final String refreshToken
  ) {
    super();
    this.accessToken = requireNonNull(accessToken);
    this.refreshToken = requireNonNull(refreshToken);
  }

}