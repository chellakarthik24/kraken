package com.kraken.config.security.client.spring;

import com.kraken.Application;
import com.kraken.config.security.client.api.SecurityClientProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SecurityClientPropertiesSpringTest {
  @Autowired
  SecurityClientProperties properties;

  @Test
  public void shouldCreateProperties() {
    assertThat(properties.getUrl()).isEqualTo("http://localhost:9080");
    assertThat(properties.getApiId()).isEqualTo("kraken-api");
    assertThat(properties.getWebId()).isEqualTo("kraken-web");
    assertThat(properties.getRealm()).isEqualTo("kraken");
  }
}
