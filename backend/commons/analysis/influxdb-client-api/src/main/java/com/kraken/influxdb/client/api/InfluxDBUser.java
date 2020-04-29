package com.kraken.influxdb.client.api;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class InfluxDBUser {
  String username;
  String password;
  String database;
}