package com.kraken.grafana.client.web;

import com.kraken.Application;
import com.kraken.config.influxdb.api.InfluxDBProperties;
import com.kraken.grafana.client.api.GrafanaAdminClient;
import com.kraken.grafana.client.api.GrafanaUser;
import com.kraken.grafana.client.api.GrafanaUserClient;
import com.kraken.grafana.client.api.GrafanaUserClientBuilder;
import com.kraken.influxdb.client.api.InfluxDBUser;
import com.kraken.security.entity.token.KrakenRole;
import com.kraken.security.entity.token.KrakenTokenUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;

import static com.google.common.collect.ImmutableList.of;

//@Ignore("Start grafana before running")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {Application.class})
@SpringBootTest
public class WebGrafanaClientIntegrationTest {

  @Autowired
  GrafanaAdminClient grafanaAdminClient;

  @Autowired
  GrafanaUserClientBuilder grafanaUserClientBuilder;

  @MockBean
  InfluxDBProperties dbProperties;

  InfluxDBUser dbUser;
  GrafanaUser grafanaUser;
  GrafanaUserClient grafanaUserClient;

  @Before
  public void setUp() {
    BDDMockito.given(dbProperties.getUrl()).willReturn("http://localhost:8086");

    dbUser = InfluxDBUser.builder()
        .username("user_kujvhjbids")
        .password("pwd_euvwh4neqh")
        .database("db_kujvhjbids")
        .build();
    grafanaUser = GrafanaUser.builder()
        .datasourceName("xif6fqnmyn")
        .email("gerald.pereira@octoperf.com")
        .username("gerald.pereira@octoperf.com")
        .password("fdfvvdv4lt")
        .id("2")
        .build();

    grafanaUserClient = grafanaUserClientBuilder.user(grafanaUser).build();
  }

//  @Test
//  public void shouldImportDashboard() throws IOException {
//    final var dashboard = ResourceUtils.getResourceContent("grafana-gatling-dashboard-integration.json");
//    final var imported = grafanaAdminClient.importDashboard(GrafanaUserTest.GRAFANA_USER, "testId", "Title", Instant.now().toEpochMilli(), dashboard).block();
//    System.out.println(imported);
//  }

  @Test
  public void shouldCreateUser() {
    final var user = grafanaAdminClient.createUser(KrakenTokenUser.builder()
        .issuedAt(Instant.EPOCH)
        .expirationTime(Instant.EPOCH.plusMillis(1))
        .userId("userId")
        .email("gerald.pereira@octoperf.com")
        .username("gerald.pereira@octoperf.com")
        .roles(of(KrakenRole.USER))
        .groups(of("/default-kraken"))
        .currentGroup("/default-kraken")
        .build()).block();
    System.out.println(user);
  }

  @Test
  public void shouldCreateDatasource() {
    grafanaUserClient.createDatasource(dbUser).block();
  }


  @Test
  public void shouldDeleteUser() {
    grafanaAdminClient.deleteUser(grafanaUser).block();
  }
}
