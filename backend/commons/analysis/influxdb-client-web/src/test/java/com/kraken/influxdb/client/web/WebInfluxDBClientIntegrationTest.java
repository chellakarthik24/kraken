package com.kraken.influxdb.client.web;

import com.kraken.Application;
import com.kraken.config.influxdb.api.InfluxDBProperties;
import com.kraken.influxdb.client.api.InfluxDBClient;
import com.kraken.tools.unique.id.IdGenerator;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.when;


//@Ignore("Start a dev InfluxDB")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class WebInfluxDBClientIntegrationTest {

  private InfluxDBClient client;

  @Autowired
  IdGenerator idGenerator;

  @MockBean
  InfluxDBProperties properties;

  @Before
  public void before() {
    when(properties.getUrl()).thenReturn("http://localhost:8086/");
    when(properties.getDatabase()).thenReturn("gatling");
    when(properties.getUser()).thenReturn("admin");
    when(properties.getPassword()).thenReturn("kraken");
    client = new WebInfluxDBClient(properties, idGenerator);
  }

  @Test
  public void shouldCreateUser() throws InterruptedException {
    System.out.println("======================>");
    System.out.println(client.createUserDB().block());
    System.out.println("<======================");
  }
}
