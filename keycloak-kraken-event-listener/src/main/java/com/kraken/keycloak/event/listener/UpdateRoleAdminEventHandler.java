package com.kraken.keycloak.event.listener;

import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

final class UpdateRoleAdminEventHandler implements AdminEventHandler {

  @Override
  public String toPath(String url, AdminEvent event) {
    return String.format("%s/admin/update_role", url);
  }

  @Override
  public boolean test(final AdminEvent event) {
    return (OperationType.DELETE.equals(event.getOperationType()) || OperationType.CREATE.equals(event.getOperationType())) && ResourceType.REALM_ROLE_MAPPING.equals(event.getResourceType());
  }
}
