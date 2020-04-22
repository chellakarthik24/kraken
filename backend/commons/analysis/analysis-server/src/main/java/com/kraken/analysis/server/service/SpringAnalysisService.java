package com.kraken.analysis.server.service;

import com.kraken.analysis.entity.DebugEntry;
import com.kraken.analysis.entity.HttpHeader;
import com.kraken.analysis.entity.Result;
import com.kraken.analysis.entity.ResultStatus;
import com.kraken.config.grafana.api.AnalysisResultsProperties;
import com.kraken.config.grafana.api.GrafanaProperties;
import com.kraken.grafana.client.api.GrafanaClient;
import com.kraken.influxdb.client.api.InfluxDBClient;
import com.kraken.security.authentication.api.AuthenticationMode;
import com.kraken.security.entity.functions.api.OwnerToApplicationId;
import com.kraken.security.entity.functions.api.OwnerToUserId;
import com.kraken.security.entity.owner.Owner;
import com.kraken.storage.client.api.StorageClient;
import com.kraken.storage.client.api.StorageClientBuilder;
import com.kraken.storage.entity.StorageNode;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.function.Function;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class SpringAnalysisService implements AnalysisService {

  private static final String RESULT_JSON = "result.json";

  @NonNull AnalysisResultsProperties properties;
  @NonNull GrafanaProperties grafana;

  @NonNull InfluxDBClient influxdbClient;
  @NonNull GrafanaClient grafanaClient;
  @NonNull StorageClientBuilder storageClientBuilder;

  @NonNull OwnerToApplicationId toApplicationId;
  @NonNull OwnerToUserId toUserId;

  @NonNull Function<List<HttpHeader>, String> headersToExtension;
  @NonNull Function<ResultStatus, Long> statusToEndDate;

  @Override
  public Mono<StorageNode> create(final Owner owner, final Result result) {
    final var storageClient = this.impersonateStorage(owner);
    final var resultPath = properties.getResultPath(result.getId());
    final var resultJsonPath = resultPath.resolve(RESULT_JSON).toString();

    final var createGrafanaReport = storageClient.getContent(grafana.getDashboard())
        .flatMap(dashboard -> grafanaClient.initDashboard(result.getId(), result.getDescription() + " - " + result.getId(), result.getStartDate(), dashboard))
        .flatMap(grafanaClient::importDashboard);

    final var createGrafanaReportOrNot = Mono.just(result).flatMap(res -> res.getType().isDebug() ? Mono.just("ok") : createGrafanaReport);

    return createGrafanaReportOrNot.flatMap(s -> storageClient.createFolder(resultPath.toString()))
        .flatMap(storageNode -> storageClient.setJsonContent(resultJsonPath, result));
  }

  @Override
  public Mono<String> delete(final Owner owner, final String resultId) {
    final var storageClient = this.impersonateStorage(owner);
    final var resultPath = properties.getResultPath(resultId);
    final var resultJsonPath = resultPath.resolve(RESULT_JSON).toString();
    final var deleteFolder = storageClient.delete(resultPath.toString());
    final var getResult = storageClient.getJsonContent(resultJsonPath, Result.class);
    final var deleteReport = getResult.flatMap(result -> result.getType().isDebug() ? Mono.just("ok") : Mono.zip(grafanaClient.deleteDashboard(resultId), influxdbClient.deleteSeries(resultId)));
    return Mono.zip(deleteFolder, deleteReport).map(objects -> resultId);
  }

  @Override
  public Mono<StorageNode> setStatus(final Owner owner, final String resultId, final ResultStatus status) {
    final var storageClient = this.impersonateStorage(owner);
    final var endDate = this.statusToEndDate.apply(status);
    final var resultPath = properties.getResultPath(resultId).resolve(RESULT_JSON).toString();

    return storageClient.getJsonContent(resultPath, Result.class)
        .filter(result -> !result.getStatus().isTerminal())
        .flatMap(result -> {
          if (result.getType().isDebug()) {
            return Mono.just(result);
          }
          return grafanaClient.getDashboard(resultId)
              .flatMap(dashboard -> grafanaClient.updatedDashboard(endDate, dashboard))
              .flatMap(grafanaClient::setDashboard)
              .map(s -> result);
        })
        .map(result -> result.withEndDate(endDate).withStatus(status))
        .flatMap(result -> storageClient.setJsonContent(resultPath, result));
  }

  @Override
  public Mono<DebugEntry> addDebug(final Owner owner, final DebugEntry debug) {
    final var storageClient = this.sessionStorage(owner);
    final var outputFolder = properties.getResultPath(debug.getResultId()).resolve("debug");

    return Mono.just(debug)
        .flatMap(debugEntry -> {
          if (!debug.getRequestBodyFile().isEmpty()) {
            final var body = debug.getRequestBodyFile();
            final var bodyFile = String.format("%s-request%s", debugEntry.getId(), this.headersToExtension.apply(debugEntry.getRequestHeaders()));
            return storageClient.setContent(outputFolder.resolve(bodyFile).toString(), body).map(s -> debugEntry.withRequestBodyFile(bodyFile));
          }
          return Mono.just(debugEntry);
        })
        .flatMap(debugEntry -> {
          if (!debug.getResponseBodyFile().isEmpty()) {
            final var body = debug.getResponseBodyFile();
            final var bodyFile = String.format("%s-response%s", debugEntry.getId(), this.headersToExtension.apply(debugEntry.getResponseHeaders()));
            return storageClient.setContent(outputFolder.resolve(bodyFile).toString(), body).map(s -> debugEntry.withResponseBodyFile(bodyFile));
          }
          return Mono.just(debugEntry);
        })
        .flatMap(debugEntry -> storageClient.setJsonContent(outputFolder.resolve(debugEntry.getId() + ".debug").toString(), debugEntry).map(storageNode -> debugEntry));
  }

  private StorageClient sessionStorage(final Owner owner) {
    final var ids = ownerToIds(owner);
    return storageClientBuilder.mode(AuthenticationMode.SESSION).applicationId(ids.getT1()).build();
  }

  private StorageClient impersonateStorage(final Owner owner) {
    final var ids = ownerToIds(owner);
    return storageClientBuilder.mode(AuthenticationMode.IMPERSONATE, ids.getT2()).applicationId(ids.getT1()).build();
  }

  private Tuple2<String, String> ownerToIds(final Owner owner) {
    final var applicationId = toApplicationId.apply(owner).orElseThrow();
    final var userId = toUserId.apply(owner).orElseThrow();
    return Tuples.of(applicationId, userId);
  }
}

