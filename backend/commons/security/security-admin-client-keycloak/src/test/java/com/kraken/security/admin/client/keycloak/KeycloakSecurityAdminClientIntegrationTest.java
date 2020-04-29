package com.kraken.security.admin.client.keycloak;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
//@Ignore("Start keycloak before running")
public class KeycloakSecurityAdminClientIntegrationTest {

  @Autowired
  private SecurityAdminClientBuilder builder;

  SecurityAdminClient client;

  @Before
  public void setUp() {
    client = builder.mode(AuthenticationMode.SERVICE_ACCOUNT).build();
  }

  @Test
  public void shouldGetUser() {
    final var krakenUser = client.getUser("2e44ffae-111c-4f59-ae2b-65000de6f7b7").block();
    assertThat(krakenUser).isNotNull();
    System.out.println(krakenUser);
  }

  @Test
  public void shouldSetUser() {
    final var krakenUser = client.getUser("2e44ffae-111c-4f59-ae2b-65000de6f7b7").block();
    assertThat(krakenUser).isNotNull();
    System.out.println(krakenUser);
    final Map<String, ? extends List<String>> attributes = ImmutableMap.<String, List<String>>builder().putAll(krakenUser.getAttributes())
        .put("foo", ImmutableList.of("bar"))
        .build();
    final var updated = krakenUser.withAttributes(attributes);
    System.out.println(updated);
    client.setUser(krakenUser.withAttributes(attributes)).block();
  }
}