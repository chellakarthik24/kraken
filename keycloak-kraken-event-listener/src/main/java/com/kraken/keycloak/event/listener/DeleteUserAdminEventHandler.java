package com.kraken.keycloak.event.listener;

import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

final class DeleteUserAdminEventHandler implements AdminEventHandler {

  @Override
  public String toPath(String url, AdminEvent event) {
    return String.format("%s/admin/%s_%s", url, event.getOperationType().name().toLowerCase(), event.getResourceType().name().toLowerCase());
  }

  @Override
  public boolean test(final AdminEvent event) {
    return OperationType.DELETE.equals(event.getOperationType()) && ResourceType.USER.equals(event.getResourceType());
  }
}
