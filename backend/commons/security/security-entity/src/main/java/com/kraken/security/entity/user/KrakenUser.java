package com.kraken.security.entity.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Optional.ofNullable;

@Value
@Builder(toBuilder = true)
public class KrakenUser {
  Map<String, String> access;
  @With
  Map<String, ? extends List<String>> attributes;
  List<KrakenUserConsent> clientConsents;
  Map<String, String> clientRoles;
  Long createdTimestamp;
  List<KrakenCredential> credentials;
  List<String> disableableCredentialTypes;
  String email;
  Boolean emailVerified;
  Boolean enabled;
  List<KrakenFederatedIdentity> federatedIdentities;
  String federationLink;
  String firstName;
  List<String> groups;
  String id;
  String lastName;
  Integer notBefore;
  String origin;
  List<String> realmRoles;
  List<String> requiredActions;
  String self;
  String serviceAccountClientId;
  String username;


  @JsonCreator
  KrakenUser(
      @JsonProperty("access") final Map<String, String> access,
      @JsonProperty("attributes") final Map<String, ? extends List<String>> attributes,
      @JsonProperty("clientConsents") final List<KrakenUserConsent> clientConsents,
      @JsonProperty("clientRoles") final Map<String, String> clientRoles,
      @JsonProperty("createdTimestamp") final Long createdTimestamp,
      @JsonProperty("credentials") final List<KrakenCredential> credentials,
      @JsonProperty("disableableCredentialTypes") final List<String> disableableCredentialTypes,
      @NonNull @JsonProperty("email") final String email,
      @JsonProperty("emailVerified") final Boolean emailVerified,
      @JsonProperty("enabled") final Boolean enabled,
      @JsonProperty("federatedIdentities") final List<KrakenFederatedIdentity> federatedIdentities,
      @JsonProperty("federationLink") final String federationLink,
      @JsonProperty("firstName") final String firstName,
      @JsonProperty("groups") final List<String> groups,
      @NonNull @JsonProperty("id") final String id,
      @JsonProperty("lastName") final String lastName,
      @JsonProperty("notBefore") final Integer notBefore,
      @JsonProperty("origin") final String origin,
      @JsonProperty("realmRoles") final List<String> realmRoles,
      @JsonProperty("requiredActions") final List<String> requiredActions,
      @JsonProperty("self") final String self,
      @JsonProperty("serviceAccountClientId") final String serviceAccountClientId,
      @NonNull @JsonProperty("username") final String username
  ) {
    super();
    this.access = ofNullable(access).orElse(ImmutableMap.of());
    this.attributes = ofNullable(attributes).orElse(ImmutableMap.of());
    this.clientConsents = ofNullable(clientConsents).orElse(ImmutableList.of());
    this.clientRoles = ofNullable(clientRoles).orElse(ImmutableMap.of());
    this.createdTimestamp = ofNullable(createdTimestamp).orElse(0L);
    this.credentials = ofNullable(credentials).orElse(ImmutableList.of());
    this.disableableCredentialTypes = ofNullable(disableableCredentialTypes).orElse(ImmutableList.of());
    this.email = email;
    this.emailVerified = ofNullable(emailVerified).orElse(false);
    this.enabled = ofNullable(enabled).orElse(false);
    this.federatedIdentities = ofNullable(federatedIdentities).orElse(ImmutableList.of());
    this.federationLink = nullToEmpty(federationLink);
    this.firstName = nullToEmpty(firstName);
    this.groups = ofNullable(groups).orElse(ImmutableList.of());
    this.id = id;
    this.lastName = nullToEmpty(lastName);
    this.notBefore = ofNullable(notBefore).orElse(0);
    this.origin = nullToEmpty(origin);
    this.realmRoles = ofNullable(realmRoles).orElse(ImmutableList.of());
    this.requiredActions = ofNullable(requiredActions).orElse(ImmutableList.of());
    this.self = nullToEmpty(self);
    this.serviceAccountClientId = nullToEmpty(serviceAccountClientId);
    this.username = username;
  }
}
