package com.kraken.storage.file;

import com.kraken.security.entity.owner.Owner;
import com.kraken.storage.entity.StorageWatcherEvent;
import reactor.core.publisher.Flux;

public interface StorageWatcherService {

  Flux<StorageWatcherEvent> watch(Owner owner);

  Flux<StorageWatcherEvent> watch(Owner owner, String root);
}
