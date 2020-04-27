package com.kraken.keycloak.event.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

class KeycloakClient {

  private final String keycloakUrl;
  private final String realm;
  private final String clientId;
  private final String clientSecret;
  private final ObjectMapper mapper;
  private final AtomicReference<String> accessToken;
  private final AtomicReference<Instant> expired;

  public KeycloakClient(final String keycloakUrl,
                        final String realm,
                        final String clientId,
                        final String clientSecret) {
    this.keycloakUrl = requireNonNull(keycloakUrl);
    this.realm = requireNonNull(realm);
    this.clientId = requireNonNull(clientId);
    this.clientSecret = requireNonNull(clientSecret);
    this.mapper = new ObjectMapper();
    this.accessToken = new AtomicReference<>();
    this.expired = new AtomicReference<>(Instant.EPOCH);
  }

  public synchronized String getAccessToken() throws IOException {
    final Instant now = Instant.now();
    if (this.expired.get().isBefore(now)) {
      System.out.println("Expired");
      final ResteasyClient client = new ResteasyClientBuilder().build();
      final ResteasyWebTarget target = client.target(String.format("%s/auth/realms/%s/protocol/openid-connect/token", keycloakUrl, realm));
      final Form form = new Form();
      form.param("client_id", clientId)
          .param("client_secret", clientSecret)
          .param("grant_type", "client_credentials");
      final Entity<Form> entity = Entity.form(form);
      final Response response = target.request(MediaType.APPLICATION_FORM_URLENCODED).post(entity);
      final String tokenString = response.readEntity(String.class);
      System.out.println(tokenString);
      response.close();
      client.close();
      final JsonNode node = mapper.reader().readTree(tokenString);

      // Access token
      final String accessToken = node.get("access_token").asText();
      System.out.println(accessToken);
      this.accessToken.set(accessToken);

      // Expires In
      final Long expiresIn = node.get("expires_in").asLong();
      System.out.println(expiresIn);
      this.expired.set(now.plusSeconds(expiresIn).minusSeconds(60L));
    }
    System.out.println("Access token: " + this.accessToken.get());
    return this.accessToken.get();
  }

}
