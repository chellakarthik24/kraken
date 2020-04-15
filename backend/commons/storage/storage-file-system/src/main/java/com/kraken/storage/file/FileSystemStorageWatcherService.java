package com.kraken.storage.file;

import com.kraken.security.entity.owner.Owner;
import com.kraken.storage.entity.StorageWatcherEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
final class FileSystemStorageWatcherService implements StorageWatcherService {

  @NonNull OwnerToPath toPath;
  @NonNull Flux<StorageWatcherEvent> watcherEventFlux;

  @Override
  public Flux<StorageWatcherEvent> watch(final Owner owner, final String root) {
    final var rootPath = toPath.apply(owner, root).toString();
    return Flux.from(watcherEventFlux)
        .filter(storageWatcherEvent -> storageWatcherEvent.getNode().getPath().startsWith(rootPath));
  }

}

