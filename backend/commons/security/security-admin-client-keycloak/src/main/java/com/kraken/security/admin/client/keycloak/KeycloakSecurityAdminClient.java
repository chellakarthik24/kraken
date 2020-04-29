package com.kraken.security.admin.client.keycloak;

import com.kraken.config.security.client.api.SecurityClientProperties;
import com.kraken.security.admin.client.api.SecurityAdminClient;
import com.kraken.security.entity.user.KrakenUser;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class KeycloakSecurityAdminClient implements SecurityAdminClient {

  WebClient webClient;
  SecurityClientProperties properties;

  @Override
  public Mono<KrakenUser> getUser(final String userId) {
    return retry(webClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(getUserUrl(userId)).build())
        .retrieve()
        .bodyToMono(KrakenUser.class), log);
  }

  private String getUserUrl(final String userId) {
    return String.format("/auth/admin/realms/%s/users/%s", this.properties.getRealm(), userId);
  }
}
