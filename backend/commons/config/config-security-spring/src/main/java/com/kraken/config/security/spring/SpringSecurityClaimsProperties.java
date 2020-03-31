package com.kraken.config.security.spring;

import com.kraken.config.security.api.SecurityClaimsProperties;
import lombok.Builder;
import lombok.Value;
import org.springframework.boot.context.properties.ConstructorBinding;

import static java.util.Optional.ofNullable;

@Value
@Builder
@ConstructorBinding
final class SpringSecurityClaimsProperties implements SecurityClaimsProperties {
  static final SpringSecurityClaimsProperties DEFAULT_CLAIMS = builder().build();

  String groups;
  String currentGroup;
  String username;

  @Builder
  SpringSecurityClaimsProperties(
      final String groups,
      final String currentGroup,
      final String username) {
    super();
    this.groups = ofNullable(groups).orElse("user_groups");
    this.currentGroup = ofNullable(currentGroup).orElse("current_group");
    this.username = ofNullable(username).orElse("preferred_username");
  }
}
