package com.kraken.keycloak.event.listener;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.lang.System.getenv;
import static java.lang.System.setOut;
import static java.util.Objects.requireNonNull;


public class KrakenEventListenerProviderFactory implements EventListenerProviderFactory {

  private KeycloakClient client;
  private String[] urls;

  @Override
  public EventListenerProvider create(KeycloakSession keycloakSession) {
    return new KrakenEventListenerProvider(this.client);
  }

  @Override
  public void init(Config.Scope scope) {
    this.client = new KeycloakClient(getenv("KRAKEN_SECURITY_URL"),
        getenv("KRAKEN_SECURITY_REALM"),
        getenv("KRAKEN_SECURITY_API_ID"),
        getenv("KRAKEN_SECURITY_API_SECRET"));
    this.urls = requireNonNull(getenv("KRAKEN_URLS")).split(",");
  }

  @Override
  public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

  }

  @Override
  public void close() {
    this.client.close();
  }

  @Override
  public String getId() {
    return "kraken_event_listener";
  }
}