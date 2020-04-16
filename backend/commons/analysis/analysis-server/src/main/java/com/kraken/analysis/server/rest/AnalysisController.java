package com.kraken.analysis.server.rest;

import com.kraken.analysis.entity.DebugEntry;
import com.kraken.analysis.entity.Result;
import com.kraken.analysis.entity.ResultStatus;
import com.kraken.analysis.server.service.AnalysisService;
import com.kraken.security.authentication.api.UserProvider;
import com.kraken.storage.entity.StorageNode;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Pattern;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController()
@RequestMapping("/result")
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
class AnalysisController {
  @NonNull AnalysisService service;
  @NonNull UserProvider userProvider;

  @PostMapping
  public Mono<StorageNode> create(@RequestHeader("ApplicationId") @Pattern(regexp = "[a-z0-9]*") final String applicationId,
                                  @RequestBody() final Result result) {
    log.info(String.format("Create result %s", result));
    return userProvider.getOwner(applicationId).flatMap(owner -> service.create(owner, result));
  }

  @DeleteMapping
  public Mono<String> delete(@RequestHeader("ApplicationId") @Pattern(regexp = "[a-z0-9]*") final String applicationId,
                             @RequestParam("resultId") final String resultId) {
    log.info(String.format("Delete result %s", resultId));
    return userProvider.getOwner(applicationId).flatMap(owner -> service.delete(owner, resultId));
  }

  @PostMapping("/status/{status}")
  public Mono<StorageNode> setStatus(@RequestHeader("ApplicationId") @Pattern(regexp = "[a-z0-9]*") final String applicationId,
                                     @RequestParam("resultId") final String resultId, @PathVariable("status") final ResultStatus status) {
    log.info(String.format("Set result %s status to %s", resultId, status));
    return userProvider.getOwner(applicationId).flatMap(owner -> service.setStatus(owner, resultId, status));
  }

  @PostMapping(value = "/debug")
  public Mono<DebugEntry> debug(@RequestHeader("ApplicationId") @Pattern(regexp = "[a-z0-9]*") final String applicationId,
                                @RequestBody() final DebugEntry debug) {
    log.info(String.format("Add debug entry %s to result %s", debug.getRequestName(), debug.getResultId()));
    return userProvider.getOwner(applicationId).flatMap(owner -> service.addDebug(owner, debug));
  }

}
