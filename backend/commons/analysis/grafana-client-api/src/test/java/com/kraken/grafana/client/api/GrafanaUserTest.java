package com.kraken.grafana.client.api;

import com.kraken.tests.utils.TestUtils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GrafanaUserTest {

  public static final GrafanaUser GRAFANA_USER = GrafanaUser.builder()
      .id("id")
      .username("username")
      .password("password")
      .email("email")
      .datasourceId("datasourceId")
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
  public void shouldWither() {
    assertThat(GRAFANA_USER.withId("other").getId()).isEqualTo("other");
    assertThat(GRAFANA_USER.withDatasourceId("other").getDatasourceId()).isEqualTo("other");
  }

}