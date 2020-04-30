package com.kraken.grafana.client.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.kraken.Application;
import com.kraken.config.grafana.api.GrafanaProperties;
import com.kraken.grafana.client.api.GrafanaClient;
import com.kraken.grafana.client.api.GrafanaUserTest;
import com.kraken.tests.utils.ResourceUtils;
import com.kraken.tools.unique.id.IdGenerator;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class WebGrafanaClientTest {

  private ObjectMapper mapper;
  private MockWebServer server;
  private GrafanaClient client;

  @MockBean
  GrafanaProperties properties;

  @MockBean
  IdGenerator idGenerator;

  @Before
  public void before() {
    mapper = new ObjectMapper();
    server = new MockWebServer();
    final String url = server.url("/").toString();
    when(properties.getUrl()).thenReturn(url);
    client = new WebGrafanaClient(properties, mapper, idGenerator);
  }

  @After
  public void tearDown() throws IOException {
    server.shutdown();
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
  public void shouldImportDashboard() throws InterruptedException, IOException {
    final var dashboard = ResourceUtils.getResourceContent("grafana-gatling-dashboard.json");
    final var initialized = ResourceUtils.getResourceContent("grafana-gatling-dashboard-result-init.json");
    final var setDashboardResponse = "response";

    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(setDashboardResponse)
    );

    final var response = client.importDashboard(GrafanaUserTest.GRAFANA_USER, "testId", "title", 42L, dashboard).block();
    assertThat(response).isEqualTo(setDashboardResponse);

    final RecordedRequest commandRequest = server.takeRequest();
    assertThat(commandRequest.getPath()).isEqualTo("/api/dashboards/import");
    final var node = mapper.readTree(commandRequest.getBody().readString(Charsets.UTF_8));
    assertThat(mapper.writeValueAsString(node.get("dashboard"))).isEqualTo(initialized);
    assertThat(node.get("overwrite").asBoolean()).isTrue();
    assertThat(node.get("folderId").asInt()).isEqualTo(0);
  }


  @Test
  public void shouldUpdatedDashboardRunning() throws IOException, InterruptedException {
    this.shouldUpdate(ResourceUtils.getResourceContent("grafana-gatling-dashboard.json"),
        ResourceUtils.getResourceContent("grafana-gatling-dashboard-result-running.json"),
        42L);

  }

  @Test
  public void shouldUpdatedDashboardRefresh() throws IOException, InterruptedException  {
    this.shouldUpdate(ResourceUtils.getResourceContent("grafana-gatling-dashboard.json"),
        ResourceUtils.getResourceContent("grafana-gatling-dashboard-result-refresh.json"),
        0L);
  }

  private void shouldUpdate(final String dashboard, final String updated, final Long endDate) throws IOException, InterruptedException {
    final var id = "id";
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody("{\"dashboard\":" + dashboard + "}")
    );
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody("response")
    );

    final var response = client.updateDashboard(id, endDate).block();
    assertThat(response).isEqualTo(response);

    final RecordedRequest getRequest = server.takeRequest();
    assertThat(getRequest.getPath()).isEqualTo("/api/dashboards/uid/" + id);

    final RecordedRequest setRequest = server.takeRequest();
    assertThat(setRequest.getPath()).isEqualTo("/api/dashboards/db");
    final var node = mapper.readTree(setRequest.getBody().readString(Charsets.UTF_8));
    assertThat(mapper.writeValueAsString(node.get("dashboard"))).isEqualTo(updated);
    assertThat(node.get("overwrite").asBoolean()).isFalse();
    assertThat(node.get("message").asText()).isNotNull();
  }
}
