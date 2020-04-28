package com.kraken.storage.user.events.listener;

import com.kraken.security.user.events.listener.UserEventsService;
import com.kraken.security.user.events.listener.UserEventsServiceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
final class StorageUserEventsService extends UserEventsServiceAdapter {

  @Override
  public Mono<String> onDeleteUser(String userId) {
    log.info(String.format("Deleting user folder for id %s", userId));
    // TODO delete user folder
    return Mono.just(userId);
  }
}
