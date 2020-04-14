package com.kraken.runtime.client.web;

import com.kraken.config.runtime.client.api.RuntimeClientProperties;
import com.kraken.runtime.client.api.RuntimeClient;
import com.kraken.runtime.client.api.RuntimeClientFactory;
import com.kraken.security.authentication.api.ExchangeFilterFactory;
import com.kraken.security.authentication.client.spring.AbstractAuthenticatedClientFactory;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class WebRuntimeClientFactory extends AbstractAuthenticatedClientFactory<RuntimeClient> implements RuntimeClientFactory {

  public WebRuntimeClientFactory(final List<ExchangeFilterFactory> exchangeFilterFactories,
                                 final RuntimeClientProperties properties) {
    super(exchangeFilterFactories, properties);
  }

  @Override
  protected RuntimeClient create(WebClient.Builder webClientBuilder) {
    // TODO add  .header("ApplicationId", applicationId) and remove OwnerToApplicationId toApplicationId;
    return new WebRuntimeClient(webClientBuilder
        .defaultHeader("ApplicationId", "TODO")
        .build());
  }
}
