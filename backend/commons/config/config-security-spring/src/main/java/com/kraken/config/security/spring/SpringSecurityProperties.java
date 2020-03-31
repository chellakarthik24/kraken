package com.kraken.config.security.spring;

import com.kraken.config.security.api.SecurityProperties;
import lombok.Builder;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import static com.kraken.config.security.spring.SpringSecurityClaimsProperties.DEFAULT_CLAIMS;
import static java.util.Optional.ofNullable;

@Value
@Builder
@ConstructorBinding
@ConfigurationProperties("kraken.security")
final class SpringSecurityProperties implements SecurityProperties {
  SpringSecurityClaimsProperties claims;

  @Builder
  SpringSecurityProperties(
      final SpringSecurityClaimsProperties claims
  ) {
    super();
    this.claims = ofNullable(claims).orElse(DEFAULT_CLAIMS);
  }
}
