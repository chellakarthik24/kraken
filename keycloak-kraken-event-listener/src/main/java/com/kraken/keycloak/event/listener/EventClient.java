package com.kraken.keycloak.event.listener;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

class EventClient {

  private final String url;

  public EventClient(final String url) {
    this.url = requireNonNull(url);
  }

  public void sendEvent(final String accessToken, final Event event) {
    System.out.println("Send event");
    final ResteasyClient client = new ResteasyClientBuilder().build();
    final ResteasyWebTarget eventTarget = client.target(String.format("%s/event/%s", url, event.getType().name()));
    final Response response = eventTarget.request(MediaType.APPLICATION_FORM_URLENCODED)
        .header("Authorization", this.authHeader(accessToken))
        .post(eventToEntity(event));
    final String str = response.readEntity(String.class);
    System.out.println(str);
    System.out.println(response.getStatus());
    response.close();
    client.close();
  }

  public void sendAdminEvent(final String accessToken, final AdminEvent event) {
    final ResteasyClient client = new ResteasyClientBuilder().build();
    final ResteasyWebTarget adminTarget = client.target(String.format("%s/admin/%s", url, event.getOperationType().name()));
    final Response response = adminTarget.request(MediaType.APPLICATION_FORM_URLENCODED)
        .header(HttpHeaders.AUTHORIZATION, this.authHeader(accessToken))
        .post(adminEventToEntity(event));
    final String str = response.readEntity(String.class);
    System.out.println(str);
    System.out.println(response.getStatus());
    response.close();
    client.close();
  }

  public boolean filterEvent(final Event event) {
    switch (event.getType()) {
      case REGISTER:
      case UPDATE_EMAIL:
        return true;
      default:
        return false;
    }
  }

  public boolean filterAdminEvent(final AdminEvent event) {
    // TODO handle Role update events
//    kraken-keycloak-dev    | 14:23:05,945 INFO  [stdout] (default task-6) Admin Event Occurred:operationType=CREATE, realmId=master, clientId=8131bbcc-3da0-46c9-8e1f-dacee98dc1b0, userId=d5439e90-f4a7-4242-8dd0-f12e214538e8, ipAddress=127.0.0.1, resourcePath=users/a83b98f2-2c52-40f3-a3a3-2276caeacd3b/role-mappings/realm
//    kraken-keycloak-dev    | 14:23:53,935 INFO  [stdout] (default task-8) Admin Event Occurred:operationType=DELETE, realmId=master, clientId=8131bbcc-3da0-46c9-8e1f-dacee98dc1b0, userId=d5439e90-f4a7-4242-8dd0-f12e214538e8, ipAddress=127.0.0.1, resourcePath=users/a83b98f2-2c52-40f3-a3a3-2276caeacd3b/role-mappings/realm
    return OperationType.DELETE.equals(event.getOperationType()) && ResourceType.USER.equals(event.getResourceType());
  }

  private String authHeader(final String accessToken) {
    return String.format("Bearer %s", accessToken);
  }

  private Entity<Form> eventToEntity(final Event event) {
    final Form form = new Form()
        .param("user_id", event.getUserId());
    switch (event.getType()) {
      case REGISTER:
        form.param("email", event.getDetails().get("email"));
        form.param("username", event.getDetails().get("username"));
      case UPDATE_EMAIL:
        form.param("updated_email", event.getDetails().get("updated_email"));
        form.param("previous_email", event.getDetails().get("previous_email"));
    }
    System.out.println(form.asMap().keySet().stream().collect(Collectors.joining()));
    return Entity.form(form);
  }

  private Entity<Form> adminEventToEntity(final AdminEvent event) {
    final Form form = new Form();
    form.param("user_id", event.getAuthDetails().getUserId());
    return Entity.form(form);
  }
}
