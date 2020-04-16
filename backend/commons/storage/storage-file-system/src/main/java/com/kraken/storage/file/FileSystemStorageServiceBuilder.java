package com.kraken.storage.file;

import com.kraken.security.entity.owner.ApplicationOwner;
import com.kraken.security.entity.owner.Owner;
import com.kraken.security.entity.owner.UserOwner;
import com.kraken.security.entity.user.KrakenRole;
import com.kraken.storage.entity.StorageWatcherEvent;
import lombok.NonNull;
import reactor.core.publisher.Flux;

import static com.kraken.security.entity.owner.OwnerType.USER;


final class FileSystemStorageServiceBuilder implements StorageServiceBuilder {

  @NonNull Flux<StorageWatcherEvent> watcherEventFlux;
  @NonNull OwnerToPath ownerToPath;

  @Override
  public StorageService build(final Owner owner) {
    final var root = ownerToPath.apply(owner);
    final var service = new FileSystemStorageService(root, new FileSystemPathToStorageNode(root), watcherEventFlux);
    if (owner.getType().equals(USER)) {
      final UserOwner userOwner = (UserOwner) owner;
      if (!userOwner.getRoles().contains(KrakenRole.ADMIN)) {
        final var applicationPath = ownerToPath.apply(ApplicationOwner.builder().applicationId(userOwner.getApplicationId()).build());
        service.init(applicationPath);
      }
    }
    return service;
  }

}
