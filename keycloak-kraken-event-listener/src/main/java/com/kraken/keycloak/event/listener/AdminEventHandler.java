package com.kraken.keycloak.event.listener;

import org.keycloak.events.admin.AdminEvent;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface AdminEventHandler extends Predicate<AdminEvent> {

  Entity<Form> toEntity(final AdminEvent event);

  String toPath(String url, AdminEvent event);

}
