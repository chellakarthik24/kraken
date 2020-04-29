package com.kraken.security.admin.client.keycloak;

import com.kraken.Application;
import com.kraken.config.security.client.api.SecurityClientProperties;
import com.kraken.security.admin.client.api.SecurityAdminClient;
import com.kraken.security.admin.client.api.SecurityAdminClientBuilder;
import com.kraken.security.authentication.api.AuthenticationMode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
//@Ignore("Start keycloak before running")
public class KeycloakSecurityAdminClientIntegrationTest  {

  @Autowired
  private SecurityAdminClientBuilder builder;

  SecurityAdminClient client;

  @Before
  public void setUp(){
    client = builder.mode(AuthenticationMode.SERVICE_ACCOUNT).build();
  }

  @Test
  public void shouldGetUser() {
    final var krakenUser = client.getUser("2e44ffae-111c-4f59-ae2b-65000de6f7b7").block();
    assertThat(krakenUser).isNotNull();
    System.out.println(krakenUser);
  }
}