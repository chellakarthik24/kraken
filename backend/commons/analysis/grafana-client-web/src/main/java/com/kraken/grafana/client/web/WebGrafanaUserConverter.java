package com.kraken.grafana.client.web;

import com.kraken.grafana.client.api.GrafanaUser;
import com.kraken.grafana.client.api.GrafanaUserConverter;
import com.kraken.security.entity.user.KrakenUser;
import org.springframework.stereotype.Component;

@Component
final class WebGrafanaUserConverter implements GrafanaUserConverter {

  @Override
  public GrafanaUser apply(final KrakenUser krakenUser) {
    return GrafanaUser.builder()
        .id(krakenUser.getAttribute(GrafanaUser.USER_ID_ATTRIBUTE))
        .username(krakenUser.getAttribute(GrafanaUser.USERNAME_ATTRIBUTE))
        .password(krakenUser.getAttribute(GrafanaUser.PASSWORD_ATTRIBUTE))
        .email(krakenUser.getAttribute(GrafanaUser.EMAIL_ATTRIBUTE))
        .datasourceId(krakenUser.getAttribute(GrafanaUser.DATASOURCE_ID_ATTRIBUTE))
        .build();
  }
}
