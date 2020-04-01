package com.kraken.security.authentication.jwt;

import com.kraken.security.authentication.jwt.JwtConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class JwtConfigurationTest {

  @Test
  public void shouldReturnSecurityContext() {
    Assertions.assertThat(new JwtConfiguration().securityContext()).isNotNull();
  }
}