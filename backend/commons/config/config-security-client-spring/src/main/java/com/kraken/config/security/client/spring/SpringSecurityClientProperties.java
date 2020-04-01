package com.kraken.config.security.client.spring;

import com.kraken.config.security.client.api.SecurityClientProperties;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Value
@Builder
@ConstructorBinding
@ConfigurationProperties("kraken.security.client")
final class SpringSecurityClientProperties implements SecurityClientProperties {

  @NonNull String url;
  @NonNull String id;
  @NonNull String secret;

}
