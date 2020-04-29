package com.kraken.analysis.server.service;

import com.kraken.analysis.entity.DebugEntry;
import com.kraken.analysis.entity.Result;
import com.kraken.analysis.entity.ResultStatus;
import com.kraken.security.entity.owner.Owner;
import com.kraken.security.user.events.listener.UserEventsService;
import com.kraken.storage.entity.StorageNode;
import reactor.core.publisher.Mono;

public interface AnalysisService extends UserEventsService {

  Mono<StorageNode> create(Owner owner, Result result);

  Mono<String> delete(Owner owner, String resultId);

  Mono<StorageNode> setStatus(Owner owner, String resultId, ResultStatus status);

  Mono<DebugEntry> addDebug(Owner owner, DebugEntry debug);

  Mono<String> grafanaLogin(Owner owner);

}

