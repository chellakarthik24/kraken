package com.kraken.config.security.exchange.filter.spring;

import com.kraken.config.security.exchange.filter.api.ExchangeFilterMode;
import com.kraken.config.security.exchange.filter.api.ExchangeFilterProperties;
import com.kraken.test.utils.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ExchangeFilterPropertiesTest {
  public static final ExchangeFilterProperties SECURITY_PROPERTIES = SpringExchangeFilterProperties.builder()
      .mode(ExchangeFilterMode.WEB)
      .build();

  @Test
  public void shouldPassToString() {
    TestUtils.shouldPassToString(SECURITY_PROPERTIES);
  }

  @Test
  public void shouldPassEqualsVerifier() {
    EqualsVerifier.forClass(SpringExchangeFilterProperties.class).verify();
  }

  @Test
  public void shouldCreate() {
    assertNotNull(SECURITY_PROPERTIES);
  }
}
