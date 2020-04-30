package com.kraken.grafana.client.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kraken.config.grafana.api.GrafanaProperties;
import com.kraken.config.influxdb.api.InfluxDBProperties;
import com.kraken.grafana.client.api.GrafanaUser;
import com.kraken.grafana.client.api.GrafanaUserClient;
import com.kraken.grafana.client.api.GrafanaUserClientBuilder;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
class WebGrafanaUserClientBuilder implements GrafanaUserClientBuilder {

  private static final String GRAFANA_SESSION = "grafana_session";

  GrafanaUser user;
  final ObjectMapper mapper;
  final GrafanaProperties grafanaProperties;
  final InfluxDBProperties dbProperties;

  public WebGrafanaUserClientBuilder(@NonNull final ObjectMapper mapper,
                                     @NonNull final GrafanaProperties grafanaProperties,
                                     @NonNull final InfluxDBProperties dbProperties) {
    this.mapper = mapper;
    this.grafanaProperties = grafanaProperties;
    this.dbProperties = dbProperties;
  }


  @Override
  public GrafanaUserClientBuilder user(final GrafanaUser user) {
    this.user = user;
    return this;
  }

  @Override
  public Mono<GrafanaUserClient> build() {
    return getSessionCookie()
        .map(sessionCookie -> new WebGrafanaUserClient(user,
            WebClient
                .builder()
                .baseUrl(grafanaProperties.getUrl())
                .defaultCookie(GRAFANA_SESSION, sessionCookie.getValue())
                .build(),
            dbProperties,
            mapper));
  }

  @Override
  public Mono<ResponseCookie> getSessionCookie() {
    final var loginClient = WebClient
        .builder()
        .baseUrl(grafanaProperties.getUrl())
        .build();
    return loginClient.post()
        .uri(uriBuilder -> uriBuilder.path("/login").build())
        .body(BodyInserters.fromValue(LoginRequest.builder()
            .email(user.getEmail())
            .password(user.getPassword())
            .user(user.getUsername())
            .build()))
        .exchange()
        .map(response -> response.cookies().getFirst(GRAFANA_SESSION));
  }
}
