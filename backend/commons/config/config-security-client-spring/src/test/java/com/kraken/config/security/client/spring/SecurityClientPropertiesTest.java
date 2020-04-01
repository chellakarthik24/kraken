package com.kraken.config.security.client.spring;

import com.kraken.config.security.client.api.SecurityClientProperties;
import com.kraken.test.utils.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SecurityClientPropertiesTest {

  public static final SecurityClientProperties SECURITY_CLIENT_PROPERTIES = SpringSecurityClientProperties.builder()
      .url("url")
      .id("kraken-api")
      .secret("chut")
      .build();

  @Test
  public void shouldPassToString() {
    TestUtils.shouldPassToString(SECURITY_CLIENT_PROPERTIES);
  }

  @Test
  public void shouldPassEqualsVerifier() {
    EqualsVerifier.forClass(SpringSecurityClientProperties.class).verify();
  }

  @Test
  public void shouldPassNPE() {
    TestUtils.shouldPassNPE(SpringSecurityClientProperties.class);
  }

  @Test
  public void shouldCreate() {
    assertNotNull(SECURITY_CLIENT_PROPERTIES);
  }
}
