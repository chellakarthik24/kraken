package com.kraken.grafana.client.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.kraken.config.grafana.api.GrafanaProperties;
import com.kraken.grafana.client.api.GrafanaClient;
import com.kraken.test.utils.ResourceUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class WebGrafanaClientTest {

  private ObjectMapper mapper;
  private MockWebServer server;
  private GrafanaClient client;

  @Mock
  GrafanaProperties properties;

  @Before
  public void before() {
    mapper = new ObjectMapper();
    server = new MockWebServer();
    final String url = server.url("/").toString();
    when(properties.getUrl()).thenReturn(url);
    client = new WebGrafanaClient(properties, mapper);
  }

  @After
  public void tearDown() throws IOException {
    server.shutdown();
  }

  @Test
  public void shouldGetDashboard() throws InterruptedException {
    final var id = "id";
    final var dashboard = "{\"refresh\":\"1s\"}";

    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody("{\"dashboard\":{\"refresh\":\"1s\"}}")
    );

    final var response = client.getDashboard(id).block();
    assertThat(response).isEqualTo(dashboard);

    final RecordedRequest commandRequest = server.takeRequest();
    assertThat(commandRequest.getPath()).isEqualTo("/api/dashboards/uid/id");
  }

  @Test
  public void shouldUpdateDashboard() throws InterruptedException, IOException {
    final var dashboard = "{\"refresh\":false}";
    final var setDashboardResponse = "response";

    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(setDashboardResponse)
    );

    final var response = client.setDashboard(dashboard).block();
    assertThat(response).isEqualTo(setDashboardResponse);

    final RecordedRequest commandRequest = server.takeRequest();
    assertThat(commandRequest.getPath()).isEqualTo("/api/dashboards/db");
    final var node = mapper.readTree(commandRequest.getBody().readString(Charsets.UTF_8));
    assertThat(mapper.writeValueAsString(node.get("dashboard"))).isEqualTo(dashboard);
    assertThat(node.get("overwrite").asBoolean()).isFalse();
    assertThat(node.get("message").asText()).isNotNull();
  }

  @Test
  public void shouldImportDashboard() throws InterruptedException, IOException {
    final var dashboard = "{\"refresh\":false}";
    final var setDashboardResponse = "response";

    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(setDashboardResponse)
    );

    final var response = client.importDashboard(dashboard).block();
    assertThat(response).isEqualTo(setDashboardResponse);

    final RecordedRequest commandRequest = server.takeRequest();
    assertThat(commandRequest.getPath()).isEqualTo("/api/dashboards/import");
    final var node = mapper.readTree(commandRequest.getBody().readString(Charsets.UTF_8));
    assertThat(mapper.writeValueAsString(node.get("dashboard"))).isEqualTo(dashboard);
    assertThat(node.get("overwrite").asBoolean()).isTrue();
    assertThat(node.get("folderId").asInt()).isEqualTo(0);
  }


  @Test
  public void shouldDeleteDashboard() throws InterruptedException {
    final var id = "id";
    final var deleteDashboardResponse = "deleteDashboardResponse";

    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(deleteDashboardResponse)
    );

    final var response = client.deleteDashboard(id).block();
    assertThat(response).isEqualTo(deleteDashboardResponse);

    final RecordedRequest commandRequest = server.takeRequest();
    assertThat(commandRequest.getPath()).isEqualTo("/api/dashboards/uid/id");
  }

  @Test
  public void shouldInitDashboard() throws IOException {
    final var result = client.initDashboard("testId", "title", 42L, ResourceUtils.getResourceContent("grafana-gatling-dashboard.json"));
    assertThat(result).isEqualTo(ResourceUtils.getResourceContent("grafana-gatling-dashboard-result-init.json"));
  }

  @Test(expected = RuntimeException.class)
  public void shouldInitDashboardFail() {
    client.initDashboard("testId", "title", 42L, "ca va fail !!!");
  }

  @Test(expected = RuntimeException.class)
  public void shouldUpdatedDashboardFail() {
    client.updatedDashboard(42L, "ca va fail !!!");
  }

  @Test
  public void shouldUpdatedDashboardRunning() throws IOException {
    final var result = client.updatedDashboard(42L, ResourceUtils.getResourceContent("grafana-gatling-dashboard.json"));
    assertThat(result).isEqualTo(ResourceUtils.getResourceContent("grafana-gatling-dashboard-result-running.json"));
  }

  @Test
  public void shouldUpdatedDashboardCompleted() throws IOException {
    final var result = client.updatedDashboard(42L, ResourceUtils.getResourceContent("grafana-gatling-dashboard.json"));
    assertThat(result).isEqualTo(ResourceUtils.getResourceContent("grafana-gatling-dashboard-result-completed.json"));
  }

  @Test
  public void shouldUpdatedDashboardRefresh() throws IOException {
    final var result = client.updatedDashboard(0L, ResourceUtils.getResourceContent("grafana-gatling-dashboard.json"));
    assertThat(result).contains("\"refresh\":\"1s\"");
  }
}
