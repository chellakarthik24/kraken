package com.kraken.storage.user.events.listener;

import com.kraken.security.user.events.listener.UserEventsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
final class StorageUserEventsService implements UserEventsService {

  @Override
  public Mono<String> onRegisterUser(String userId, String email, String username) {
    return Mono.just(userId);
  }

  @Override
  public Mono<String> onUpdateEmail(String userId, String updatedEmail, String previousEmail) {
    return Mono.just(userId);
  }

  @Override
  public Mono<String> onDeleteUser(String userId) {
    log.info(String.format("Deleting user folder for id %s", userId));
    // TODO
    return Mono.just(userId);
  }
}
