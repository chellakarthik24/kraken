package com.kraken.security.client.keycloak;

import com.kraken.Application;
import com.kraken.security.client.api.SecurityClient;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
//@Ignore("Start keycloak before running")
public class KeycloakSecurityClientIntegrationTest {

  @Autowired
  SecurityClient client;

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void shouldLoginExchangeRefresh() {
    final var loginToken = client.userLogin("kraken-user", "kraken").block();
    Assertions.assertThat(loginToken).isNotNull();
    System.out.println(loginToken);
    final var apiToken = client.exchangeToken(loginToken).block();
    Assertions.assertThat(apiToken).isNotNull();
    System.out.println(apiToken);
    final var refreshedToken = client.refreshToken(apiToken).block();
    Assertions.assertThat(refreshedToken).isNotNull();
    System.out.println(refreshedToken);
  }
}