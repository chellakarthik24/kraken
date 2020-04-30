package com.kraken.influxdb.client.api;

import com.kraken.tools.webclient.Client;
import reactor.core.publisher.Mono;

public interface InfluxDBClient extends Client {

  Mono<String> deleteSeries(String database, String testId);

  Mono<InfluxDBUser> createUser();

  Mono<Void> deleteUser(InfluxDBUser user);
}
