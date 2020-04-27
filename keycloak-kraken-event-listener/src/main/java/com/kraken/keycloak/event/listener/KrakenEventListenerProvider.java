package com.kraken.keycloak.event.listener;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KrakenEventListenerProvider implements EventListenerProvider {

  private final KeycloakClient client;
  private final ExecutorService executor;

  public KrakenEventListenerProvider(final KeycloakClient client) {
    this.client = client;
    this.executor = Executors.newCachedThreadPool();
  }

  @Override
  public void onEvent(final Event event) {
    System.out.println("Event Occurred:" + toString(event));
    // TODO filter register user event (update ?)
//    switch (event.getType()) {
//      case LOGIN:
//        this.executor.submit(() -> {
//          System.out.println(this.client.getAccessToken());
//        });
//        break;
//    }
  }

//  [stdout] (default task-8) Event Occurred:type=REGISTER, realmId=kraken, clientId=account, userId=405b81ee-98c8-4e18-b6b8-963402341eeb, ipAddress=127.0.0.1, auth_method=openid-connect, auth_type=code, register_method=form, redirect_uri=http://localhost:9080/auth/realms/kraken/account/login-redirect, code_id=36656fcb-c171-4261-91a8-b3097b6342b4, email=kojiro.sazaki@gmail.com, username=kojiro.sazaki@gmail.com
//  kraken-keycloak-dev    | 09:33:23,742 INFO  [stdout] (default task-8) Event Occurred:type=LOGIN, realmId=kraken, clientId=account, userId=405b81ee-98c8-4e18-b6b8-963402341eeb, ipAddress=127.0.0.1, auth_method=openid-connect, auth_type=code, redirect_uri=http://localhost:9080/auth/realms/kraken/account/login-redirect, consent=no_consent_required, code_id=36656fcb-c171-4261-91a8-b3097b6342b4, username=kojiro.sazaki@gmail.com
// [stdout] (default task-8) Event Occurred:type=UPDATE_PROFILE, realmId=kraken, clientId=account, userId=405b81ee-98c8-4e18-b6b8-963402341eeb, ipAddress=127.0.0.1
// [stdout] (default task-8) Event Occurred:type=UPDATE_EMAIL, realmId=kraken, clientId=account, userId=405b81ee-98c8-4e18-b6b8-963402341eeb, ipAddress=127.0.0.1, updated_email=gerald.rapiere@gmail.com, previous_email=kojiro.sazaki@gmail.com
// kraken-keycloak-dev    | 09:36:57,561 INFO  [stdout] (default task-8) Admin Event Occurred:operationType=DELETE, realmId=master, clientId=672ba65e-3f97-4e81-a7ee-bca5deb990f6, userId=affa9342-80d7-4609-be07-cb2446fa4c93, ipAddress=127.0.0.1, resourcePath=users/405b81ee-98c8-4e18-b6b8-963402341eeb

  @Override
  public void onEvent(final AdminEvent adminEvent, boolean b) {
    System.out.println("Admin Event Occurred:" + toString(adminEvent));
    // TODO filter delete user event
  }

  @Override
  public void close() {
    this.executor.shutdown();
  }

  private String toString(final Event event) {

    StringBuilder sb = new StringBuilder();


    sb.append("type=");

    sb.append(event.getType());

    sb.append(", realmId=");

    sb.append(event.getRealmId());

    sb.append(", clientId=");

    sb.append(event.getClientId());

    sb.append(", userId=");

    sb.append(event.getUserId());

    sb.append(", ipAddress=");

    sb.append(event.getIpAddress());


    if (event.getError() != null) {

      sb.append(", error=");

      sb.append(event.getError());

    }


    if (event.getDetails() != null) {

      for (Map.Entry<String, String> e : event.getDetails().entrySet()) {

        sb.append(", ");

        sb.append(e.getKey());

        if (e.getValue() == null || e.getValue().indexOf(' ') == -1) {

          sb.append("=");

          sb.append(e.getValue());

        } else {

          sb.append("='");

          sb.append(e.getValue());

          sb.append("'");

        }

      }

    }


    return sb.toString();

  }


  private String toString(AdminEvent adminEvent) {

    StringBuilder sb = new StringBuilder();


    sb.append("operationType=");

    sb.append(adminEvent.getOperationType());

    sb.append(", realmId=");

    sb.append(adminEvent.getAuthDetails().getRealmId());

    sb.append(", clientId=");

    sb.append(adminEvent.getAuthDetails().getClientId());

    sb.append(", userId=");

    sb.append(adminEvent.getAuthDetails().getUserId());

    sb.append(", ipAddress=");

    sb.append(adminEvent.getAuthDetails().getIpAddress());

    sb.append(", resourcePath=");

    sb.append(adminEvent.getResourcePath());


    if (adminEvent.getError() != null) {

      sb.append(", error=");

      sb.append(adminEvent.getError());

    }


    return sb.toString();

  }
}