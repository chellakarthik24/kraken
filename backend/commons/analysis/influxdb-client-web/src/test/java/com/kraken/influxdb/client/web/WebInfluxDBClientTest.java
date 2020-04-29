package com.kraken.influxdb.client.web;

import com.google.common.base.Charsets;
import com.kraken.config.influxdb.api.InfluxDBProperties;
import com.kraken.influxdb.client.api.InfluxDBClient;
import com.kraken.influxdb.client.web.WebInfluxDBClient;
import com.kraken.tools.unique.id.IdGenerator;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class WebInfluxDBClientTest {

  private MockWebServer server;
  private InfluxDBClient client;

  @Mock
  InfluxDBProperties properties;

  @Mock
  IdGenerator idGenerator;

  @Before
  public void before() {
    server = new MockWebServer();
    when(properties.getUrl()).thenReturn(server.url("/").toString());
    when(properties.getDatabase()).thenReturn("influxdbDatabase");
    when(properties.getUser()).thenReturn("root");
    when(properties.getPassword()).thenReturn("admin");
    client = new WebInfluxDBClient(properties, idGenerator);
  }

  @After
  public void tearDown() throws IOException {
    server.shutdown();
  }

  @Test
  public void shouldDeleteSeries() throws InterruptedException {
    final var testId = "testId";

    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody("{}")
    );

    final var response = client.deleteSeries(testId).block();
    assertThat(response).isEqualTo("{}");

    final RecordedRequest commandRequest = server.takeRequest();
    assertThat(commandRequest.getPath()).isEqualTo("/query?db=influxdbDatabase");
    assertThat(commandRequest.getBody().readString(Charsets.UTF_8)).isEqualTo("q=DROP+SERIES+FROM+%2F.*%2F+WHERE+test+%3D+%27testId%27");
  }
}
