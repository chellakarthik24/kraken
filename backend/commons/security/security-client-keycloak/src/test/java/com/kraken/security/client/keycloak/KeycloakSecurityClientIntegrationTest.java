package com.kraken.security.client.keycloak;

import com.kraken.Application;
import com.kraken.config.security.client.api.SecurityClientProperties;
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

  @Autowired
  SecurityClientProperties properties;

  @Test
  public void shouldClientLogin() {
    final var loginToken = client.clientLogin(properties.getApi()).block();
    Assertions.assertThat(loginToken).isNotNull();
    System.out.println(loginToken);
  }

  @Test
  public void shouldLoginExchangeRefresh() {
    final var loginToken = client.userLogin(properties.getWeb(), "kraken-user", "kraken").block();
    Assertions.assertThat(loginToken).isNotNull();
    System.out.println(loginToken);
    final var containerToken = client.exchangeToken(properties.getContainer(), loginToken.getAccessToken()).block();
    Assertions.assertThat(containerToken).isNotNull();
    System.out.println(containerToken);
    final var refreshedToken = client.refreshToken(properties.getContainer(), containerToken.getRefreshToken()).block();
    Assertions.assertThat(refreshedToken).isNotNull();
    System.out.println(refreshedToken);
    final var refreshedToken2 = client.refreshToken(properties.getContainer(), containerToken.getRefreshToken()).block();
    Assertions.assertThat(refreshedToken2).isNotNull();
    System.out.println(refreshedToken2);
  }
}