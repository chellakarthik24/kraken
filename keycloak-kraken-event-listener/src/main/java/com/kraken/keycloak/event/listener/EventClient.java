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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.util.Objects.requireNonNull;

class EventClient {

  private final ResteasyClient client;
  private final ResteasyWebTarget eventTarget;
  private final ResteasyWebTarget adminTarget;

  public EventClient(final String url) {
    this.client = new ResteasyClientBuilder().build();
    this.eventTarget = client.target(String.format("%s/event", requireNonNull(url)));
    this.adminTarget = client.target(String.format("%s/admin", requireNonNull(url)));
  }

  public void sendEvent(final String accessToken, final Event event) {
    final Response response = eventTarget.request(MediaType.APPLICATION_FORM_URLENCODED)
        .header("Authorization", this.authHeader(accessToken))
        .post(eventToEntity(event));
    response.close();
  }

  public void sendAdminEvent(final String accessToken, final AdminEvent event) {
    final Response response = adminTarget.request(MediaType.APPLICATION_FORM_URLENCODED)
        .header("Authorization", this.authHeader(accessToken))
        .post(adminEventToEntity(event));
    response.close();
  }

  //  [stdout] (default task-8) Event Occurred:type=REGISTER, realmId=kraken, clientId=account, userId=405b81ee-98c8-4e18-b6b8-963402341eeb, ipAddress=127.0.0.1, auth_method=openid-connect, auth_type=code, register_method=form, redirect_uri=http://localhost:9080/auth/realms/kraken/account/login-redirect, code_id=36656fcb-c171-4261-91a8-b3097b6342b4, email=kojiro.sazaki@gmail.com, username=kojiro.sazaki@gmail.com
//  kraken-keycloak-dev    | 09:33:23,742 INFO  [stdout] (default task-8) Event Occurred:type=LOGIN, realmId=kraken, clientId=account, userId=405b81ee-98c8-4e18-b6b8-963402341eeb, ipAddress=127.0.0.1, auth_method=openid-connect, auth_type=code, redirect_uri=http://localhost:9080/auth/realms/kraken/account/login-redirect, consent=no_consent_required, code_id=36656fcb-c171-4261-91a8-b3097b6342b4, username=kojiro.sazaki@gmail.com
// [stdout] (default task-8) Event Occurred:type=UPDATE_PROFILE, realmId=kraken, clientId=account, userId=405b81ee-98c8-4e18-b6b8-963402341eeb, ipAddress=127.0.0.1
// [stdout] (default task-8) Event Occurred:type=UPDATE_EMAIL, realmId=kraken, clientId=account, userId=405b81ee-98c8-4e18-b6b8-963402341eeb, ipAddress=127.0.0.1, updated_email=gerald.rapiere@gmail.com, previous_email=kojiro.sazaki@gmail.com
// kraken-keycloak-dev    | 09:36:57,561 INFO  [stdout] (default task-8) Admin Event Occurred:operationType=DELETE, realmId=master, clientId=672ba65e-3f97-4e81-a7ee-bca5deb990f6, userId=affa9342-80d7-4609-be07-cb2446fa4c93, ipAddress=127.0.0.1, resourcePath=users/405b81ee-98c8-4e18-b6b8-963402341eeb


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
    return OperationType.DELETE.equals(event.getOperationType()) && ResourceType.USER.equals(event.getResourceType());
  }

  private String authHeader(final String accessToken) {
    return String.format("Bearer %s", accessToken);
  }

  private Entity<Form> eventToEntity(final Event event) {
    final Form form = new Form();
    form.param("type", event.getType().name());
    form.param("user_id", event.getUserId());
    switch (event.getType()) {
      case REGISTER:
        form.param("email", event.getDetails().get("email"));
        form.param("username", event.getDetails().get("username"));
      case UPDATE_EMAIL:
        form.param("updated_email", event.getDetails().get("updated_email"));
        form.param("previous_email", event.getDetails().get("previous_email"));
    }
    return Entity.form(form);
  }

  private Entity<Form> adminEventToEntity(final AdminEvent event) {
    final Form form = new Form();
    form.param("type", event.getOperationType().name());
    form.param("user_id", event.getAuthDetails().getUserId());
    return Entity.form(form);
  }
}
