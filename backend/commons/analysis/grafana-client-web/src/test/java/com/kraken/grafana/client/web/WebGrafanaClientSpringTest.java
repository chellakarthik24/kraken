package com.kraken.grafana.client.web;

import com.kraken.Application;
import com.kraken.grafana.client.api.GrafanaClient;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class WebGrafanaClientSpringTest {

  @Autowired
  GrafanaClient grafanaClient;

  @Test
  public void shouldCreateWebClients() {
    Assertions.assertThat(grafanaClient).isNotNull();
  }

}