package com.kraken.runtime.client.web;

import com.kraken.config.runtime.client.api.RuntimeClientProperties;
import com.kraken.runtime.client.api.RuntimeClient;
import com.kraken.runtime.client.api.RuntimeClientFactory;
import com.kraken.security.authentication.api.ExchangeFilterFactory;
import com.kraken.security.authentication.client.spring.AbstractAuthenticatedClientFactory;
import com.kraken.security.entity.functions.api.OwnerToApplicationId;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class WebRuntimeClientFactory extends AbstractAuthenticatedClientFactory<RuntimeClient> implements RuntimeClientFactory {

  OwnerToApplicationId toApplicationId;

  public WebRuntimeClientFactory(final List<ExchangeFilterFactory> exchangeFilterFactories,
                                 final RuntimeClientProperties properties,
                                 @NonNull final OwnerToApplicationId toApplicationId) {
    super(exchangeFilterFactories, properties);
    this.toApplicationId = toApplicationId;
  }

  @Override
  protected RuntimeClient create(WebClient.Builder webClientBuilder) {
    return new WebRuntimeClient(webClientBuilder.build(), toApplicationId);
  }
}
