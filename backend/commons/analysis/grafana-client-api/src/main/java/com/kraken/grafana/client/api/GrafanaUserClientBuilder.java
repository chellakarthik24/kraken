package com.kraken.grafana.client.api;

import org.springframework.http.ResponseCookie;
import reactor.core.publisher.Mono;

public interface GrafanaUserClientBuilder {

  GrafanaUserClientBuilder user(GrafanaUser user);

  Mono<GrafanaUserClient> build();

  Mono<ResponseCookie> getSessionCookie();
}
