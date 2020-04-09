package com.kraken.runtime.container.executor;

import com.kraken.runtime.client.api.RuntimeClient;
import com.kraken.runtime.client.api.RuntimeClientFactory;
import com.kraken.security.authentication.api.AuthenticationMode;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContainerExecutorConfiguration {

  @Bean
  public RuntimeClient runtimeClient(@NonNull final RuntimeClientFactory factory) {
    return factory.create(AuthenticationMode.CONTAINER);
  }

}
