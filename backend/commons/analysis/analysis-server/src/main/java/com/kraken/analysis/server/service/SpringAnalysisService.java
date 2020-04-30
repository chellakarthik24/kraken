package com.kraken.analysis.server.service;

import com.kraken.analysis.entity.DebugEntry;
import com.kraken.analysis.entity.HttpHeader;
import com.kraken.analysis.entity.Result;
import com.kraken.analysis.entity.ResultStatus;
import com.kraken.config.grafana.api.AnalysisResultsProperties;
import com.kraken.config.grafana.api.GrafanaProperties;
import com.kraken.grafana.client.api.GrafanaClient;
import com.kraken.influxdb.client.api.InfluxDBClient;
import com.kraken.security.admin.client.api.SecurityAdminClient;
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
  @NonNull SecurityAdminClient adminClient;

  @NonNull OwnerToApplicationId toApplicationId;
  @NonNull OwnerToUserId toUserId;

  // TODO create interfaces
  @NonNull Function<List<HttpHeader>, String> headersToExtension;
  @NonNull Function<ResultStatus, Long> statusToEndDate;

  @Override
  public Mono<StorageNode> create(final Owner owner, final Result result) {
    final var storageClient = this.impersonateStorage(owner);
    final var resultPath = properties.getResultPath(result.getId());
    final var resultJsonPath = resultPath.resolve(RESULT_JSON).toString();

    final var createGrafanaReport = storageClient.getContent(grafana.getDashboard())
        .flatMap(dashboard -> grafanaClient.importDashboard(result.getId(), result.getDescription() + " - " + result.getId(), result.getStartDate(), dashboard));

    final var createGrafanaReportOrNot = Mono.just(result).flatMap(res -> res.getType().isDebug() ? Mono.just("ok") : createGrafanaReport);

    return createGrafanaReportOrNot.flatMap(s -> storageClient.createFolder(resultPath.toString()))
        .flatMap(storageNode -> storageClient.setJsonContent(resultJsonPath, result));
  }

  @Override
  public Mono<String> delete(final Owner owner, final String resultId) {
    final var userId = this.toUserId.apply(owner);
    final var storageClient = this.impersonateStorage(owner);
    final var resultPath = properties.getResultPath(resultId);
    final var resultJsonPath = resultPath.resolve(RESULT_JSON).toString();
    final var deleteFolder = storageClient.delete(resultPath.toString());
    final var getResult = storageClient.getJsonContent(resultJsonPath, Result.class);
    final var getDatabase = "";// TODO adminClient.getUser(userId).map(user -> user.getAttribute())
    final var deleteReport = getResult.flatMap(result -> result.getType().isDebug() ? Mono.just("ok") : Mono.zip(grafanaClient.deleteDashboard(resultId), influxdbClient.deleteSeries(getDatabase, resultId)));
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
          return grafanaClient.updateDashboard(resultId, endDate)
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

  @Override
  public Mono<String> grafanaLogin(Owner owner) {
    // TODO get the user from SecurityAdminClient
    // TODO fetch its grafana credentials (email/username and password)
    // TODO connect to grafana
    // TODO forward the setCookie header to the client
    // TODO return the grafana login URL
    return null;
  }

  @Override
  public Mono<String> onRegisterUser(String userId, String email, String username) {
    // TODO create a 'databaseUserPassword'
    // TODO create a user in influxDB login ?
    // TODO create a 'dashboardUserPassword'
    // TODO create a user in grafana
    // TODO add a datasource in grafana
    // TODO Update user with the passwords, dashboardUserId, dashboardDatasourceId
    // Or analysisUserPassword/login/id etc ?
    return null;
  }

  @Override
  public Mono<String> onUpdateEmail(String userId, String updatedEmail, String previousEmail) {
    // TODO update database login ?
    // TODO update grafana email
    return null;
  }

  @Override
  public Mono<String> onDeleteUser(String userId) {
    // TODO get the user from SecurityAdminClient
    // TODO Delete user and datasource in grafana
    // TODO Delete user in influxdb
    return null;
  }

  @Override
  public Mono<String> onCreateRole(String userId, String role) {
    // TODO set role in grafana
    return null;
  }

  @Override
  public Mono<String> onDeleteRole(String userId, String role) {
    // TODO set role in grafana
    return null;
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

