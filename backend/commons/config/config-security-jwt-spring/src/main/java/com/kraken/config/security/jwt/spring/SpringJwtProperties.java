package com.kraken.config.security.jwt.spring;

import com.kraken.config.security.jwt.api.JwtProperties;
import lombok.Builder;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import static java.util.Optional.ofNullable;

@Value
@Builder
@ConstructorBinding
@ConfigurationProperties("kraken.security.authentication")
final class SpringJwtProperties implements JwtProperties {
  SpringJwtClaimsProperties claims;

  @Builder
  SpringJwtProperties(
      final SpringJwtClaimsProperties claims
  ) {
    super();
    this.claims = ofNullable(claims).orElse(SpringJwtClaimsProperties.DEFAULT_CLAIMS);
  }
}
