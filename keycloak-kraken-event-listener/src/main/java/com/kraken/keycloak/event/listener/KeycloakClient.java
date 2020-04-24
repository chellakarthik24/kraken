package com.kraken.keycloak.event.listener;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;

final class KeycloakClient {

  private final String keycloakUrl;
  private final String realm;
  private final String clientId;
  private final String clientSecret;

  public KeycloakClient(final String keycloakUrl,
                        final String realm,
                        final String clientId,
                        final String clientSecret) {
    this.keycloakUrl = Objects.requireNonNull(keycloakUrl);
    this.clientId = Objects.requireNonNull(clientId);
    this.clientSecret = Objects.requireNonNull(clientSecret);
    this.realm = Objects.requireNonNull(realm);
  }

  public String getAccessToken() {
    // TODO Check expiration and refresh token
    final ResteasyClient client = new ResteasyClientBuilder().build();
    final ResteasyWebTarget target = client.target(String.format("%s/auth/realms/%s/protocol/openid-connect/token", keycloakUrl, realm));
    final Form form = new Form();
    form.param("client_id", clientId)
        .param("client_secret", clientSecret)
        .param("grant_type", "client_credentials");
    final Entity<Form> entity = Entity.form(form);
    Response response = target.request(MediaType.APPLICATION_FORM_URLENCODED).post(entity);
    String value = response.readEntity(String.class);
    response.close();
    // TODO return token not the whole JSON
    return value;
  }
}
