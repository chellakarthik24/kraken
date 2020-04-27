package com.kraken.keycloak.event.listener;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

final class KeycloakClient {

  private final String keycloakUrl;
  private final String realm;
  private final String clientId;
  private final String clientSecret;
  private final AtomicReference<String> accessToken;
  private final AtomicBoolean expired;
  private final ScheduledExecutorService executorService;

  public KeycloakClient(final String keycloakUrl,
                        final String realm,
                        final String clientId,
                        final String clientSecret) {
    this.keycloakUrl = Objects.requireNonNull(keycloakUrl);
    this.clientId = Objects.requireNonNull(clientId);
    this.clientSecret = Objects.requireNonNull(clientSecret);
    this.realm = Objects.requireNonNull(realm);
    this.accessToken = new AtomicReference<>();
    this.expired = new AtomicBoolean(true);
    this.executorService = Executors.newSingleThreadScheduledExecutor();
  }

  public synchronized String getAccessToken() {
    if (this.expired.get()) {
      final ResteasyClient client = new ResteasyClientBuilder().build();
      final ResteasyWebTarget target = client.target(String.format("%s/auth/realms/%s/protocol/openid-connect/token", keycloakUrl, realm));
      final Form form = new Form();
      form.param("client_id", clientId)
          .param("client_secret", clientSecret)
          .param("grant_type", "client_credentials");
      final Entity<Form> entity = Entity.form(form);
      Response response = target.request(MediaType.APPLICATION_FORM_URLENCODED).post(entity);
      final KeycloakToken token = response.readEntity(KeycloakToken.class);
      response.close();
      this.accessToken.set(token.accessToken);
      this.expired.set(false);
      executorService.schedule(() -> this.expired.set(true), token.expiresIn - 60L, TimeUnit.SECONDS);
    }
    return this.accessToken.get();
  }

  public void close(){
      this.executorService.shutdown();
  }
}
