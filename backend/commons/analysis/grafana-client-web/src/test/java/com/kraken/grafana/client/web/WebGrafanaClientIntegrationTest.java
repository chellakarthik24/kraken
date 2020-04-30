package com.kraken.grafana.client.web;

import com.kraken.Application;
import com.kraken.grafana.client.api.GrafanaClient;
import com.kraken.grafana.client.api.GrafanaUser;
import com.kraken.grafana.client.api.GrafanaUserTest;
import com.kraken.influxdb.client.api.InfluxDBUser;
import com.kraken.security.entity.token.KrakenRole;
import com.kraken.security.entity.token.KrakenTokenUser;
import com.kraken.security.entity.token.KrakenTokenUserTest;
import com.kraken.tests.utils.ResourceUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.Instant;

import static com.google.common.collect.ImmutableList.of;

//@Ignore("Start grafana before running")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {Application.class})
@SpringBootTest
public class WebGrafanaClientIntegrationTest {

  @Autowired
  WebGrafanaClient grafanaClient;

  @Test
  public void shouldImportDashboard() throws IOException {
    final var dashboard = ResourceUtils.getResourceContent("grafana-gatling-dashboard-integration.json");
    final var imported = grafanaClient.importDashboard(GrafanaUserTest.GRAFANA_USER, "testId", "Title", Instant.now().toEpochMilli(), dashboard).block();
    System.out.println(imported);
  }

  @Test
  public void shouldCreateUser() {
    final var user = grafanaClient.createUser(KrakenTokenUser.builder()
        .issuedAt(Instant.EPOCH)
        .expirationTime(Instant.EPOCH.plusMillis(1))
        .userId("userId")
        .email("gerald.pereira+test2@octoperf.com")
        .username("gpe2")
        .roles(of(KrakenRole.USER))
        .groups(of("/default-kraken"))
        .currentGroup("/default-kraken")
        .build(), null).block();
    System.out.println(user);
  }

  @Test
  public void shouldCreateDatasource() {
    final var user = grafanaClient.createDatasource(
        GrafanaUser.builder()
            .id("5")
            .username("gpe2")
            .email("gerald.pereira+test2@octoperf.com")
            .password("0d2nqf7fwh")
            .datasourceId("")
            .build(),
        InfluxDBUser.builder()
            .username("user_clq6vuli1j")
            .password("pwd_eqjlzqfnz7")
            .database("db_clq6vuli1j")
            .build()).block();
    System.out.println(user);
  }


  @Test
  public void shouldDeleteUser() {
    grafanaClient.deleteUser(GrafanaUser.builder()
        .id("2")
        .username("")
        .email("")
        .password("")
        .datasourceId("")
        .build()).block();
  }
}
