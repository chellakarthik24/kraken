package com.kraken.grafana.client.api;

import reactor.core.publisher.Mono;

import java.util.List;

public interface GrafanaUserClientBuilder {

  GrafanaUserClientBuilder user(GrafanaUser user);

  Mono<GrafanaUserClient> build();

  Mono<List<String>> getSessionCookie();
}
