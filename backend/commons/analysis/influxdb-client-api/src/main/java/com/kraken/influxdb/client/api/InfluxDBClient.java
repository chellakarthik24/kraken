package com.kraken.influxdb.client.api;

import reactor.core.publisher.Mono;

public interface InfluxDBClient {

  Mono<String> deleteSeries(String testId);

  Mono<InfluxDBUser> createUserDB();

  Mono<Void> deleteUserDB(InfluxDBUser user);
}
