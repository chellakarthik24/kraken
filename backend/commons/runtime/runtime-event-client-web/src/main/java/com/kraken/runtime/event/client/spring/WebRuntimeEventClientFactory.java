package com.kraken.runtime.event.client.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kraken.config.runtime.client.api.RuntimeClientProperties;
import com.kraken.runtime.event.client.api.RuntimeEventClient;
import com.kraken.security.authentication.api.ExchangeFilterFactory;
import com.kraken.security.authentication.client.spring.AbstractAuthenticatedClientFactory;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class WebRuntimeEventClientFactory extends AbstractAuthenticatedClientFactory<RuntimeEventClient> {

  @NonNull ObjectMapper mapper;

  public WebRuntimeEventClientFactory(final List<ExchangeFilterFactory> exchangeFilterFactories,
                                      final RuntimeClientProperties properties,
                                      final ObjectMapper mapper) {
    super(exchangeFilterFactories, properties);
    this.mapper = requireNonNull(mapper);
  }

  @Override
  protected RuntimeEventClient create(WebClient.Builder webClientBuilder) {
    return new WebRuntimeEventClient(webClientBuilder.build(), mapper);
  }
}
