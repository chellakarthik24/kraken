package com.kraken.analysis.configuration.container;

import com.kraken.analysis.client.api.AnalysisClient;
import com.kraken.analysis.client.api.AnalysisClientFactory;
import com.kraken.security.authentication.api.AuthenticationMode;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AnalysisClientContainerConfiguration {

  @Bean
  public AnalysisClient analysisClient(@NonNull final AnalysisClientFactory factory) {
    return factory.create(AuthenticationMode.CONTAINER);
  }

}
