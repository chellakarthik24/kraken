package com.kraken.analysis.client.web;

import com.kraken.analysis.client.api.AnalysisClient;
import com.kraken.config.analysis.client.api.AnalysisClientProperties;
import com.kraken.security.authentication.api.ExchangeFilter;
import com.kraken.security.authentication.api.ExchangeFilterFactory;
import com.kraken.security.authentication.client.spring.AbstractAuthenticatedClientFactory;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class WebAnalysisClientFactory extends AbstractAuthenticatedClientFactory<AnalysisClient> {

  public WebAnalysisClientFactory(final List<ExchangeFilterFactory> exchangeFilterFactories,
                                  final AnalysisClientProperties properties) {
    super(exchangeFilterFactories, properties);

  }

  @Override
  protected AnalysisClient create(WebClient.Builder webClientBuilder) {
    return new WebAnalysisClient(webClientBuilder.build());
  }
}
