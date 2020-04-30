package com.kraken.grafana.client.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kraken.config.grafana.api.GrafanaProperties;
import com.kraken.config.influxdb.api.InfluxDBProperties;
import com.kraken.grafana.client.api.GrafanaUser;
import com.kraken.grafana.client.api.GrafanaUserClient;
import com.kraken.grafana.client.api.GrafanaUserClientBuilder;
import com.kraken.tools.unique.id.IdGenerator;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
class WebGrafanaUserClientBuilder implements GrafanaUserClientBuilder {

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
  public GrafanaUserClient build() {
    return new WebGrafanaUserClient(user, grafanaProperties, dbProperties, mapper);
  }
}
