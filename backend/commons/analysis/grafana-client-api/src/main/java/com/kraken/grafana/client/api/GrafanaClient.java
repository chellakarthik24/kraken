package com.kraken.grafana.client.api;

import com.kraken.influxdb.client.api.InfluxDBUser;
import com.kraken.security.entity.token.KrakenTokenUser;
import com.kraken.tools.webclient.Client;
import reactor.core.publisher.Mono;

public interface GrafanaClient extends Client {

  Mono<String> importDashboard(GrafanaUser user,
                               String testId,
                               String title,
                               Long startDate,
                               String dashboard);

  Mono<String> updateDashboard(String testId,
                               Long endDate);

  Mono<String> deleteDashboard(String testId);

  Mono<GrafanaUser> createUser(KrakenTokenUser tokenUser,
                               InfluxDBUser dbUser);

  Mono<Void> deleteUser(GrafanaUser user);

  Mono<String> login(GrafanaUser user);

}
