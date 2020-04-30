package com.kraken.influxdb.client.web;

import com.kraken.Application;
import com.kraken.config.influxdb.api.InfluxDBProperties;
import com.kraken.influxdb.client.api.InfluxDBClient;
import com.kraken.influxdb.client.api.InfluxDBUser;
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
  public void shouldCreateUserDB() {
    System.out.println("======================>");
    System.out.println(client.createUserDB().block());
    System.out.println("<======================");
  }

  @Test
  public void shouldDropUserDB() {
    final var user = InfluxDBUser.builder()
        .username("user_dj2wifpbfo")
        .password("")
        .database("db_dux2ksox8z")
        .build();
    client.deleteUserDB(user).block();
  }
}
