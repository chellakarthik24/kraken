package com.kraken.grafana.client.api;

import com.kraken.security.entity.token.KrakenTokenUser;
import com.kraken.tools.webclient.Client;
import reactor.core.publisher.Mono;

public interface GrafanaAdminClient extends Client {

  Mono<GrafanaUser> createUser(KrakenTokenUser tokenUser);

  Mono<Void> deleteUser(GrafanaUser user);

  Mono<String> login(GrafanaUser user);

}
