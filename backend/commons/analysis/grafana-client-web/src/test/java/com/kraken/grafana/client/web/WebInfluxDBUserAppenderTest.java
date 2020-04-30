package com.kraken.grafana.client.web;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.kraken.grafana.client.api.GrafanaUser.*;
import static com.kraken.grafana.client.api.GrafanaUserTest.GRAFANA_USER;
import static com.kraken.security.entity.user.KrakenUserTest.KRAKEN_USER;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebGrafanaUserAppender.class)
public class WebInfluxDBUserAppenderTest {

  @Autowired
  WebGrafanaUserAppender appender;

  @Test
  public void shouldTest() {
    Assertions.assertThat(appender.test(KRAKEN_USER)).isFalse();
    Assertions.assertThat(appender.test(KRAKEN_USER.withAttributes(ImmutableMap.of(USERNAME_ATTRIBUTE, ImmutableList.of("username"))))).isTrue();
  }

  @Test
  public void shouldApply() {
    Assertions.assertThat(appender.apply(KRAKEN_USER, GRAFANA_USER).getAttributes()).isEqualTo(
        ImmutableMap.of(
            USER_ID_ATTRIBUTE, ImmutableList.of(GRAFANA_USER.getId()),
            USERNAME_ATTRIBUTE, ImmutableList.of(GRAFANA_USER.getUsername()),
            PASSWORD_ATTRIBUTE, ImmutableList.of(GRAFANA_USER.getPassword()),
            EMAIL_ATTRIBUTE, ImmutableList.of(GRAFANA_USER.getEmail()),
            DATASOURCE_ID_ATTRIBUTE, ImmutableList.of(GRAFANA_USER.getDatasourceId())
        )
    );

  }
}