package com.kraken.influxdb.client.web;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.kraken.influxdb.client.api.InfluxDBUser;
import com.kraken.influxdb.client.api.InfluxDBUserAppender;
import com.kraken.security.entity.user.KrakenUser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
final class WebInfluxDBUserAppender implements InfluxDBUserAppender {

  @Override
  public boolean test(final KrakenUser krakenUser) {
    return krakenUser.hasAttribute(InfluxDBUser.USERNAME_ATTRIBUTE);
  }

  @Override
  public KrakenUser apply(final KrakenUser krakenUser, final InfluxDBUser user) {
    final var currentAttributes = krakenUser.getAttributes();
    final var builder = ImmutableMap.<String, List<String>>builder()
        .putAll(currentAttributes)
        .put(InfluxDBUser.USERNAME_ATTRIBUTE, ImmutableList.of(user.getUsername()))
        .put(InfluxDBUser.PASSWORD_ATTRIBUTE, ImmutableList.of(user.getPassword()))
        .put(InfluxDBUser.DATABASE_ATTRIBUTE, ImmutableList.of(user.getDatabase()));
    return krakenUser.withAttributes(builder.build());
  }
}
