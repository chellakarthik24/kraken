package com.kraken.keycloak.event.listener;

import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class RoleAdminEventHandler implements AdminEventHandler {

  private final OperationType operationType;

  public RoleAdminEventHandler(OperationType operationType) {
    this.operationType = operationType;
  }

  @Override
  public String toPath(String url, AdminEvent event) {
    return String.format("%s/admin/%s_role", url, operationType.name().toLowerCase());
  }

  @Override
  public Entity<Form> toEntity(final AdminEvent event) {
    final Form form = new Form()
        .param("user_id", event.getAuthDetails().getUserId())
        .param("role", toRole(event));
    return Entity.form(form);
  }

  private String toRole(final AdminEvent event) {
    final String representation = event.getRepresentation();
    final Pattern pattern = Pattern.compile("\"name\":\"([^\"]+)\"");
    final Matcher matcher = pattern.matcher(representation);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return "";
  }

  @Override
  public boolean test(final AdminEvent event) {
    return operationType.equals(event.getOperationType()) && ResourceType.REALM_ROLE_MAPPING.equals(event.getResourceType());
  }
}
