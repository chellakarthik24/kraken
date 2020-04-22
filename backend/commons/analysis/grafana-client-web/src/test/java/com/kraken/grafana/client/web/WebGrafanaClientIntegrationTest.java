package com.kraken.grafana.client.web;

import com.kraken.Application;
import com.kraken.grafana.client.api.GrafanaClient;
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

//@Ignore("Start keycloak and grafana before running")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {Application.class})
@SpringBootTest
public class WebGrafanaClientIntegrationTest {

  @Autowired
  GrafanaClient grafanaClient;

  @Test
  public void shouldImportDashboard()  throws IOException {
    final var dashboard = ResourceUtils.getResourceContent("grafana-gatling-dashboard-integration.json");
    final var initialized = grafanaClient.initDashboard("testId", "Title", Instant.now().toEpochMilli(), dashboard).block();
    final var imported = grafanaClient.importDashboard(initialized).block();
    System.out.println(imported);
  }
}
