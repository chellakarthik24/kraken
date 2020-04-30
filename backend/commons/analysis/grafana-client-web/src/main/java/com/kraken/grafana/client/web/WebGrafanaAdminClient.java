package com.kraken.grafana.client.web;

import com.kraken.config.grafana.api.GrafanaProperties;
import com.kraken.grafana.client.api.GrafanaAdminClient;
import com.kraken.grafana.client.api.GrafanaUser;
import com.kraken.influxdb.client.api.InfluxDBUser;
import com.kraken.security.entity.token.KrakenTokenUser;
import com.kraken.tools.unique.id.IdGenerator;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Component
final class WebGrafanaAdminClient implements GrafanaAdminClient {

  WebClient webClient;
  IdGenerator idGenerator;

  WebGrafanaAdminClient(@NonNull final GrafanaProperties properties,
                        @NonNull final IdGenerator idGenerator) {
    this.webClient = WebClient
        .builder()
        .baseUrl(properties.getUrl())
        .defaultHeader(HttpHeaders.AUTHORIZATION, basicAuthorizationHeader(properties.getUser(), properties.getPassword()))
        .build();
    this.idGenerator = idGenerator;
  }

  @Override
  public Mono<GrafanaUser> createUser(final KrakenTokenUser tokenUser) {
    final var password = idGenerator.generate();
    final var createUser = retry(this.webClient.post()
        .uri(uriBuilder -> uriBuilder.path("/api/admin/users").build())
        .body(BodyInserters.fromValue(CreateGrafanaUserRequest.builder()
            .name(tokenUser.getUsername())
            .email(tokenUser.getEmail())
            .login(tokenUser.getUsername())
            .password(password)
            .build()))
        .retrieve()
        .bodyToMono(CreateGrafanaUserResponse.class), log);

    // TODO
//      Create Organization
    //  Create tokens
    // https://grafana.com/docs/grafana/latest/tutorials/api_org_token_howto/

    return createUser.map(response -> GrafanaUser.builder()
        .id(response.getId().toString())
        .username(tokenUser.getUsername())
        .password(password)
        .email(tokenUser.getEmail())
        .datasourceName(idGenerator.generate())
        .build()
    );
  }

  @Override
  public Mono<Void> deleteUser(final GrafanaUser user) {
    final var deleteUser = retry(webClient.delete()
        .uri(uriBuilder -> uriBuilder.path("/api/admin/users/{id}").build(user.getId()))
        .retrieve()
        .bodyToMono(Void.class), log);

    final var deleteDatasource = retry(webClient.delete()
        .uri(uriBuilder -> uriBuilder.path("/api/datasources/name/{name}").build(user.getDatasourceName()))
        .retrieve()
        .bodyToMono(Void.class), log);

    return deleteUser.then(deleteDatasource);
  }

}
