package com.kraken.grafana.client.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kraken.config.grafana.api.GrafanaProperties;
import com.kraken.grafana.client.api.GrafanaClient;
import com.kraken.grafana.client.api.GrafanaClientBuilder;
import com.kraken.security.authentication.api.ExchangeFilterFactory;
import com.kraken.security.authentication.client.spring.AbstractAuthenticatedClientBuilder;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class WebGrafanaClientBuilder extends AbstractAuthenticatedClientBuilder<GrafanaClient, GrafanaProperties> implements GrafanaClientBuilder {

  ObjectMapper mapper;

  public WebGrafanaClientBuilder(final List<ExchangeFilterFactory> exchangeFilterFactories,
                                 final GrafanaProperties properties,
                                 @NonNull final ObjectMapper mapper) {
    super(exchangeFilterFactories, properties);
    this.mapper = mapper;
  }

  @Override
  public GrafanaClient build() {
    return new WebGrafanaClient(webClientBuilder.build(), mapper);
  }

}
