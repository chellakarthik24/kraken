package com.kraken.config.security.container.spring;

import com.kraken.config.security.container.api.SecurityContainerProperties;
import lombok.Builder;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

@Value
@ConstructorBinding
@ConfigurationProperties("kraken.security")
final class SpringSecurityContainerProperties implements SecurityContainerProperties {

  String accessToken;
  String refreshToken;
  Long minValidity; // In seconds

  @Builder(toBuilder = true)
  SpringSecurityContainerProperties(final String accessToken,
                                    final String refreshToken,
                                    final Long minValidity) {
    this.accessToken = requireNonNull(accessToken);
    this.refreshToken = requireNonNull(refreshToken);
    this.minValidity = ofNullable(minValidity).orElse(60L);
  }

}
