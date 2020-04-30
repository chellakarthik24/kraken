package com.kraken.grafana.client.api;

public interface GrafanaUserClientBuilder {

  GrafanaUserClientBuilder user(GrafanaUser user);

  GrafanaUserClient build();
}
