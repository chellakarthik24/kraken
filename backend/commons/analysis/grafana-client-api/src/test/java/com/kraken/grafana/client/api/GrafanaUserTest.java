package com.kraken.grafana.client.api;

import com.kraken.tests.utils.TestUtils;
import org.junit.Test;

public class GrafanaUserTest {

  public static final GrafanaUser GRAFANA_USER = GrafanaUser.builder()
      .id("id")
      .username("username")
      .password("password")
      .email("email")
      .datasourceName("datasourceName")
      .build();


  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(GRAFANA_USER.getClass());
  }

  @Test
  public void shouldPassToString() {
    TestUtils.shouldPassToString(GRAFANA_USER);
  }

  @Test
  public void shouldTestNPE() {
    TestUtils.shouldPassNPE(GrafanaUser.class);
  }

}