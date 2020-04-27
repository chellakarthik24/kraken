package com.kraken.security.user.events.listener;

import reactor.core.publisher.Mono;

public interface UserEventsService {
  Mono<String> onRegisterUser(String userId, String email, String username);

  Mono<String> onUpdateEmail(String userId, String updatedEmail, String previousEmail);

  Mono<String> onDeleteUser(String userId);
}
