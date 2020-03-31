package com.kraken.config.security.spring;

import com.kraken.config.security.api.SecurityProperties;
import com.kraken.test.utils.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SecurityPropertiesTest {

  public static final SecurityProperties SECURITY_PROPERTIES = SpringSecurityProperties.builder().build();

  @Test
  public void shouldPassToString() {
    TestUtils.shouldPassToString(SECURITY_PROPERTIES);
  }

  @Test
  public void shouldPassEqualsVerifier() {
    EqualsVerifier.forClass(SpringSecurityProperties.class).verify();
  }

  @Test
  public void shouldCreate() {
    assertNotNull(SECURITY_PROPERTIES);
  }
}
