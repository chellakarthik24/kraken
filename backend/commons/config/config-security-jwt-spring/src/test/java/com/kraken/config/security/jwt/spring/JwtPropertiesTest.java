package com.kraken.config.security.jwt.spring;

import com.kraken.config.security.jwt.api.JwtProperties;
import com.kraken.test.utils.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class JwtPropertiesTest {

  public static final JwtProperties JWT_PROPERTIES = SpringJwtProperties.builder().build();

  @Test
  public void shouldPassToString() {
    TestUtils.shouldPassToString(JWT_PROPERTIES);
  }

  @Test
  public void shouldPassEqualsVerifier() {
    EqualsVerifier.forClass(SpringJwtProperties.class).verify();
  }

  @Test
  public void shouldCreate() {
    assertNotNull(JWT_PROPERTIES);
  }
}
