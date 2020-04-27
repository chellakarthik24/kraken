package com.kraken.keycloak.event.listener;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

class KeycloakClient {

  private final ResteasyClient client;
  private final ResteasyWebTarget target;
  private final Entity<Form> entity;
  private final AtomicReference<String> accessToken;
  private final AtomicBoolean expired;
  private final ScheduledExecutorService executorService;

  public KeycloakClient(final String keycloakUrl,
                        final String realm,
                        final String clientId,
                        final String clientSecret) {
    this.client = new ResteasyClientBuilder().build();
    this.target = client.target(String.format("%s/auth/realms/%s/protocol/openid-connect/token", requireNonNull(keycloakUrl), requireNonNull(realm)));
    final Form form = new Form();
    form.param("client_id", requireNonNull(clientId))
        .param("client_secret", requireNonNull(clientSecret))
        .param("grant_type", "client_credentials");
    this.entity = Entity.form(form);
    this.accessToken = new AtomicReference<>();
    this.expired = new AtomicBoolean(true);
    this.executorService = Executors.newSingleThreadScheduledExecutor();
  }

  public synchronized String getAccessToken() {
    if (this.expired.get()) {
      final Response response = target.request(MediaType.APPLICATION_FORM_URLENCODED).post(entity);
      final KeycloakToken token = response.readEntity(KeycloakToken.class);
      response.close();
      this.accessToken.set(token.accessToken);
      this.expired.set(false);
      executorService.schedule(() -> this.expired.set(true), token.expiresIn - 60L, TimeUnit.SECONDS);
    }
    return this.accessToken.get();
  }

  public void close() {
    this.executorService.shutdown();
  }
}
