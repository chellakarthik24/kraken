package com.kraken.config.security.exchange.filter.spring;

import com.kraken.Application;
import com.kraken.config.security.exchange.filter.api.ExchangeFilterMode;
import com.kraken.config.security.exchange.filter.api.ExchangeFilterProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ExchangeFilterPropertiesSpringTest {

  @Autowired
  ExchangeFilterProperties properties;

  @Test
  public void shouldLoadProperties() {
    assertThat(properties).isNotNull();
    assertThat(properties.getMode()).isEqualTo(ExchangeFilterMode.API);
    assertThat(properties.getToken()).isEqualTo(Optional.of("token"));
  }
}
