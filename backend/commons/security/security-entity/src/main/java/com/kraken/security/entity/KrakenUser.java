package com.kraken.security.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.of;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class KrakenUser {

  String username;
  String userId;
  List<KrakenRole> roles;
  List<String> groups;
  String currentGroup;
  Instant expirationTime;
  Instant issuedAt;
  String sessionId;

  @JsonCreator
  KrakenUser(
      @JsonProperty("exp") final Long expirationTime,
      @JsonProperty("iat") final Long issuedAt,
      @JsonProperty("sub") final String userId,
      @JsonProperty("session_state") final String sessionId,
      @JsonProperty("preferred_username") final String username,
      @JsonProperty("realm_access") final Map<String, List<String>> realmAccess,
      @JsonProperty("user_groups") final List<String> groups,
      @JsonProperty("current_group") final String currentGroup
  ) {
    super();
    this.username = nullToEmpty(username);
    this.userId = requireNonNull(userId);
    this.roles = ofNullable(ofNullable(realmAccess)
        .orElse(ImmutableMap.of("roles", of())).get("roles"))
        .orElse(of())
        .stream().filter(role -> Arrays.stream(KrakenRole.values()).anyMatch(krakenRole -> krakenRole.toString().equals(role)))
        .map(KrakenRole::valueOf).collect(Collectors.toUnmodifiableList());
    this.groups = ofNullable(groups).orElse(of());
    this.currentGroup = nullToEmpty(currentGroup);
    this.expirationTime = Instant.ofEpochMilli(requireNonNull(expirationTime));
    this.issuedAt = Instant.ofEpochMilli(requireNonNull(issuedAt));
    this.sessionId = nullToEmpty(sessionId);
  }

  @Builder
  KrakenUser(
      String username,
      String userId,
      List<KrakenRole> roles,
      List<String> groups,
      String currentGroup,
      Instant expirationTime,
      Instant issuedAt,
      String sessionId
  ) {
    super();
    this.username = requireNonNull(username);
    this.userId = requireNonNull(userId);
    this.roles = requireNonNull(roles);
    this.groups = requireNonNull(groups);
    this.currentGroup = requireNonNull(currentGroup);
    this.expirationTime = requireNonNull(expirationTime);
    this.issuedAt = requireNonNull(issuedAt);
    this.sessionId = requireNonNull(sessionId);
  }
}
