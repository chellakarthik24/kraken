package com.kraken.keycloak.event.listener;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

class UpdateRoleAdminEventHandlerTest {

  private AdminEventHandler handler;

  @BeforeEach
  void setUp() {
    handler = new UpdateRoleAdminEventHandler();
  }

  @Test
  void shouldToPath() {
    final AdminEvent event = new AdminEvent();
    Assertions.assertEquals("url/admin/update_role", handler.toPath("url", event));
  }

  @Test
  void shouldTestTrueDelete() {
    final AdminEvent event = new AdminEvent();
    event.setOperationType(OperationType.DELETE);
    event.setResourceType(ResourceType.REALM_ROLE_MAPPING);
    Assertions.assertTrue(handler.test(event));
  }

  @Test
  void shouldTestTrueCreate() {
    final AdminEvent event = new AdminEvent();
    event.setOperationType(OperationType.CREATE);
    event.setResourceType(ResourceType.REALM_ROLE_MAPPING);
    Assertions.assertTrue(handler.test(event));
  }

  @Test
  void shouldTestFalseOperation() {
    final AdminEvent event = new AdminEvent();
    event.setOperationType(OperationType.UPDATE);
    event.setResourceType(ResourceType.REALM_ROLE_MAPPING);
    Assertions.assertFalse(handler.test(event));
  }

  @Test
  void shouldTestFalseResourceType() {
    final AdminEvent event = new AdminEvent();
    event.setOperationType(OperationType.CREATE);
    event.setResourceType(ResourceType.AUTH_EXECUTION);
    Assertions.assertFalse(handler.test(event));
  }

}