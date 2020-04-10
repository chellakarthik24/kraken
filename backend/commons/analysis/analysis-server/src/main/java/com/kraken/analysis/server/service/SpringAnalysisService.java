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
import com.kraken.storage.client.api.StorageClient;
import com.kraken.storage.client.api.StorageClientFactory;
import com.kraken.storage.entity.StorageNode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class SpringAnalysisService implements AnalysisService {

  // TODO
  private static final String USER_ID = "2e44ffae-111c-4f59-ae2b-65000de6f7b7";
  private static final String RESULT_JSON = "result.json";

  AnalysisResultsProperties properties;
  GrafanaProperties grafana;

  GrafanaClient grafanaClient;
  InfluxDBClient influxdbClient;
  StorageClient sessionStorageClient;
  Function<String, StorageClient> impersonateStorageClient;

  Function<List<HttpHeader>, String> headersToExtension;
  Function<ResultStatus, Long> statusToEndDate;

  SpringAnalysisService(@NonNull AnalysisResultsProperties properties,
                        @NonNull GrafanaProperties grafana,
                        @NonNull StorageClientFactory storageClientFactory,
                        @NonNull GrafanaClient grafanaClient,
                        @NonNull InfluxDBClient influxdbClient,
                        @NonNull Function<List<HttpHeader>, String> headersToExtension,
                        @NonNull Function<ResultStatus, Long> statusToEndDate) {
    this.properties = properties;
    this.grafana = grafana;
    this.grafanaClient = grafanaClient;
    this.influxdbClient = influxdbClient;
    this.headersToExtension = headersToExtension;
    this.statusToEndDate = statusToEndDate;
    this.sessionStorageClient = storageClientFactory.create(AuthenticationMode.SESSION);
    this.impersonateStorageClient = userId -> storageClientFactory.create(AuthenticationMode.IMPERSONATE, userId);
  }


  @Override
  public Mono<StorageNode> create(final Result result) {
    final var storageClient = impersonateStorageClient.apply(USER_ID);
    final var resultPath = properties.getResultPath(result.getId());
    final var resultJsonPath = resultPath.resolve(RESULT_JSON).toString();

    final var createGrafanaReport = storageClient.getContent(grafana.getDashboard())
        .map(dashboard -> grafanaClient.initDashboard(result.getId(), result.getDescription() + " - " + result.getId(), result.getStartDate(), dashboard))
        .flatMap(grafanaClient::importDashboard);

    final var createGrafanaReportOrNot = Mono.just(result).flatMap(res -> res.getType().isDebug() ? Mono.just("ok") : createGrafanaReport);

    return createGrafanaReportOrNot.flatMap(s -> storageClient.createFolder(resultPath.toString()))
        .flatMap(storageNode -> storageClient.setJsonContent(resultJsonPath, result));
  }

  @Override
  public Mono<String> delete(final String resultId) {
    final var storageClient = impersonateStorageClient.apply(USER_ID);
    final var resultPath = properties.getResultPath(resultId);
    final var resultJsonPath = resultPath.resolve(RESULT_JSON).toString();
    final var deleteFolder = storageClient.delete(resultPath.toString());
    final var getResult = storageClient.getJsonContent(resultJsonPath, Result.class);
    final var deleteReport = getResult.flatMap(result -> result.getType().isDebug() ? Mono.just("ok") : Mono.zip(grafanaClient.deleteDashboard(resultId), influxdbClient.deleteSeries(resultId)));
    return Mono.zip(deleteFolder, deleteReport).map(objects -> resultId);
  }

  @Override
  public Mono<StorageNode> setStatus(final String resultId, final ResultStatus status) {
    final var storageClient = impersonateStorageClient.apply(USER_ID);
    final var endDate = this.statusToEndDate.apply(status);
    final var resultPath = properties.getResultPath(resultId).resolve(RESULT_JSON).toString();

    return storageClient.getJsonContent(resultPath, Result.class)
        .filter(result -> !result.getStatus().isTerminal())
        .flatMap(result -> {
          if (result.getType().isDebug()) {
            return Mono.just(result);
          }
          return grafanaClient.getDashboard(resultId)
              .map(dashboard -> grafanaClient.updatedDashboard(endDate, dashboard))
              .flatMap(grafanaClient::setDashboard)
              .map(s -> result);
        })
        .map(result -> result.withEndDate(endDate).withStatus(status))
        .flatMap(result -> storageClient.setJsonContent(resultPath, result));
  }

  @Override
  public Mono<DebugEntry> addDebug(final DebugEntry debug) {
    final var outputFolder = properties.getResultPath(debug.getResultId()).resolve("debug");

    return Mono.just(debug)
        .flatMap(debugEntry -> {
          if (!debug.getRequestBodyFile().isEmpty()) {
            final var body = debug.getRequestBodyFile();
            final var bodyFile = String.format("%s-request%s", debugEntry.getId(), this.headersToExtension.apply(debugEntry.getRequestHeaders()));
            return sessionStorageClient.setContent(outputFolder.resolve(bodyFile).toString(), body).map(s -> debugEntry.withRequestBodyFile(bodyFile));
          }
          return Mono.just(debugEntry);
        })
        .flatMap(debugEntry -> {
          if (!debug.getResponseBodyFile().isEmpty()) {
            final var body = debug.getResponseBodyFile();
            final var bodyFile = String.format("%s-response%s", debugEntry.getId(), this.headersToExtension.apply(debugEntry.getResponseHeaders()));
            return sessionStorageClient.setContent(outputFolder.resolve(bodyFile).toString(), body).map(s -> debugEntry.withResponseBodyFile(bodyFile));
          }
          return Mono.just(debugEntry);
        })
        .flatMap(debugEntry -> sessionStorageClient.setJsonContent(outputFolder.resolve(debugEntry.getId() + ".debug").toString(), debugEntry).map(storageNode -> debugEntry));
  }

}

