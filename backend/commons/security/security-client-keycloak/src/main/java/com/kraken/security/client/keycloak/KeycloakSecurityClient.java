package com.kraken.security.client.keycloak;

import com.kraken.config.security.client.api.SecurityClientProperties;
import com.kraken.security.client.api.SecurityClient;
import com.kraken.security.entity.KrakenToken;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class KeycloakSecurityClient implements SecurityClient {

  public static final int NUM_RETRIES = 5;
  public static final Duration FIRST_BACKOFF = Duration.ofMillis(100);

  WebClient webClient;
  SecurityClientProperties properties;

  KeycloakSecurityClient(final SecurityClientProperties properties) {
    this.properties = requireNonNull(properties);
    this.webClient = WebClient
        .builder()
        .baseUrl(properties.getUrl())
        .build();
  }

  @Override
  public Mono<KrakenToken> userLogin(final String username, final String password) {
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(this.getOpenIdTokenUrl()).build())
        .body(BodyInserters.fromFormData("username", username)
            .with("password", password)
            .with("grant_type", "password")
            .with("client_id", properties.getWebId()))
        .retrieve()
        .bodyToMono(KrakenToken.class)
        .retryBackoff(NUM_RETRIES, FIRST_BACKOFF);
  }

  @Override
  public Mono<KrakenToken> exchangeToken(final String accessToken) {
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(this.getOpenIdTokenUrl()).build())
        .body(BodyInserters.fromFormData("client_id", properties.getApiId())
            .with("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange")
            .with("subject_token", accessToken)
            .with("requested_token_type", "urn:ietf:params:oauth:token-type:refresh_token")
            .with("audience", properties.getApiId()))
        .retrieve()
        .bodyToMono(KrakenToken.class)
        .retryBackoff(NUM_RETRIES, FIRST_BACKOFF);
  }

  @Override
  public Mono<KrakenToken> refreshToken(final String refreshToken) {
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(this.getOpenIdTokenUrl()).build())
        .body(BodyInserters.fromFormData("grant_type", "refresh_token")
            .with("refresh_token", refreshToken)
            .with("client_id", properties.getApiId()))
        .retrieve()
        .bodyToMono(KrakenToken.class)
        .retryBackoff(NUM_RETRIES, FIRST_BACKOFF);
  }

  private String getOpenIdTokenUrl() {
    return String.format("/auth/realms/%s/protocol/openid-connect/token", this.properties.getRealm());
  }
}
