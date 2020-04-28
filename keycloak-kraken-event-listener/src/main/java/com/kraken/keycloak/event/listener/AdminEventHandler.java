package com.kraken.keycloak.event.listener;

import org.keycloak.events.admin.AdminEvent;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import java.util.function.Predicate;

public interface AdminEventHandler extends Predicate<AdminEvent> {

  default Entity<Form> toEntity(final AdminEvent event) {
    final Form form = new Form();
    form.param("user_id", event.getAuthDetails().getUserId());
    return Entity.form(form);
  }

  String toPath(String url, AdminEvent event);

}
