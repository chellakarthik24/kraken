package com.kraken.security.user.events.listener;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController()
@RequestMapping("/user-events")
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
class UserEventsController {
  @NonNull UserEventsService service;

  @PostMapping(value = "/event/REGISTER", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public Mono<String> register(@RequestParam("user_id") final String userId,
                               @RequestParam("email") final String email,
                               @RequestParam("username") final String username) {
    log.info(String.format("Register user id: %s, email: %s, username: %s", userId, email, username));
    return service.onRegisterUser(userId, email, username);
  }

  @PostMapping(value = "/event/UPDATE_EMAIL")
  public Mono<String> updateEmail(final ServerWebExchange payload) {
    //  https://github.com/spring-projects/spring-framework/issues/20738
    return payload.getFormData().flatMap(data ->  {
      final var userId = data.getFirst("user_id");
      final var updatedEmail = data.getFirst("updated_email");
      final var previousEmail = data.getFirst("previous_email");
      log.info(String.format("Update user email id: %s, updated: %s, previous: %s", userId, updatedEmail, previousEmail));
      return service.onUpdateEmail(userId, updatedEmail, previousEmail);
    });
  }

  @PostMapping(value = "/admin/DELETE", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public Mono<String> delete(@RequestParam("user_id") final String userId) {
    log.info(String.format("Delete user %s", userId));
    return service.onDeleteUser(userId);
  }

  // TODO updateRole
  // TODO ServerWebExchange de partout
  // TODO toLowercase sur les paths

}