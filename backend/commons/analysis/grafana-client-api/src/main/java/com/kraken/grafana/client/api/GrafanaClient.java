package com.kraken.grafana.client.api;

import com.kraken.security.authentication.client.api.AuthenticatedClient;
import reactor.core.publisher.Mono;

public interface GrafanaClient extends AuthenticatedClient {

  Mono<String> getDashboard(String testId);

  Mono<String> setDashboard(String dashboard);

  Mono<String> importDashboard(String dashboard);

  Mono<String> deleteDashboard(String testId);

  Mono<String> initDashboard(String testId,
                       String title,
                       Long startDate,
                       String dashboard);

  Mono<String> updatedDashboard(Long endDate,
                          String dashboard);

}
