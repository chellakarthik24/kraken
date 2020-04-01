package com.kraken.config.security.exchange.filter.spring;

import com.kraken.config.security.exchange.filter.api.ExchangeFilterMode;
import com.kraken.config.security.exchange.filter.api.ExchangeFilterProperties;
import lombok.Builder;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

@Value
@ConstructorBinding
@ConfigurationProperties("kraken.security.exchange.filter")
final class SpringExchangeFilterProperties implements ExchangeFilterProperties {

  ExchangeFilterMode mode;
  Optional<String> token;


  @Builder
  SpringExchangeFilterProperties(final ExchangeFilterMode mode,
                                 final String token) {
    this.mode = requireNonNull(mode);
    this.token = ofNullable(token);
  }

}
