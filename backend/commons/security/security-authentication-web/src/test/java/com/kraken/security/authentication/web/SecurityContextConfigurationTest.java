package com.kraken.security.authentication.web;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class SecurityContextConfigurationTest {

  @Test
  public void shouldReturnSecurityContext() {
    Assertions.assertThat(new SecurityContextConfiguration().securityContext()).isNotNull();
  }
}