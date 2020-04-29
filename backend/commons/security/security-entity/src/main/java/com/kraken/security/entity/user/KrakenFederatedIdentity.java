package com.kraken.security.entity.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import static com.google.common.base.Strings.nullToEmpty;

@Value
@Builder(toBuilder = true)
public class KrakenFederatedIdentity {
  String identityProvider;
  String userId;
  String userName;

  @JsonCreator
  KrakenFederatedIdentity(
      @JsonProperty("identityProvider") final String identityProvider,
      @JsonProperty("userId") final String userId,
      @JsonProperty("userName") final String userName
  ) {
    super();
    this.identityProvider = nullToEmpty(identityProvider);
    this.userId = nullToEmpty(userId);
    this.userName = nullToEmpty(userName);
  }
}
