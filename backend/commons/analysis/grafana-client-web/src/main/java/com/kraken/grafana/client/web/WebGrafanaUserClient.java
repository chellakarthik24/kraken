package com.kraken.grafana.client.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kraken.config.grafana.api.GrafanaProperties;
import com.kraken.config.influxdb.api.InfluxDBProperties;
import com.kraken.grafana.client.api.GrafanaUser;
import com.kraken.grafana.client.api.GrafanaUserClient;
import com.kraken.influxdb.client.api.InfluxDBUser;
import com.kraken.tools.unique.id.IdGenerator;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Function;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class WebGrafanaUserClient implements GrafanaUserClient {

  GrafanaUser user;
  WebClient webClient;
  ObjectMapper mapper;
  SimpleDateFormat format;
  InfluxDBProperties dbProperties;

  WebGrafanaUserClient(@NonNull final GrafanaUser user,
                       @NonNull final WebClient webClient,
                       @NonNull final InfluxDBProperties dbProperties,
                       @NonNull final ObjectMapper mapper) {
    this.user = user;
    this.dbProperties = dbProperties;
    this.webClient = webClient;
    this.mapper = mapper;
    //  2019-03-22T10:01:00.000Z
    this.format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  @Override
  public Mono<Void> createDatasource(final InfluxDBUser dbUser) {
    final var createDatasource = retry(webClient.post()
        .uri(uriBuilder -> uriBuilder.path("/api/datasources").build())
        .body(BodyInserters.fromValue(CreateGrafanaDatasourceRequest.builder()
            .name(user.getDatasourceName())
            .access("proxy")
            .type("influxdb")
            .isDefault(true)
            .build()))
        .retrieve()
        .bodyToMono(String.class), log);

    final Function<String, Mono<Tuple2<Integer, String>>> updateDatasource = (final String response) -> Mono.fromCallable(() -> {
      System.out.println(response);
      final JsonNode responseNode = mapper.readTree(response);
      final ObjectNode datasourceNode = (ObjectNode) responseNode.get("datasource");
      datasourceNode.put("url", dbProperties.getUrl());
      datasourceNode.put("user", dbUser.getUsername());
      datasourceNode.put("database", dbUser.getDatabase());

      final ObjectNode jsonDataNode = (ObjectNode) datasourceNode.get("jsonData");
      jsonDataNode.put("httpMode", "POST");

      final ObjectNode secureJsonDataNode = mapper.createObjectNode();
      secureJsonDataNode.put("password", dbUser.getPassword());
      datasourceNode.set("secureJsonData", secureJsonDataNode);
      final var updated = mapper.writeValueAsString(datasourceNode);
      System.out.println(updated);
      return Tuples.of(datasourceNode.get("id").asInt(), updated);
    });

    final Function<Tuple2<Integer, String>, Mono<Void>> putDatasource = (final Tuple2<Integer, String> datasource) -> retry(webClient.put()
        .uri(uriBuilder -> uriBuilder.path("/api/datasources/{id}").build(datasource.getT1()))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(BodyInserters.fromValue(datasource.getT2()))
        .retrieve()
        .bodyToMono(Void.class), log);

    return createDatasource
        .flatMap(updateDatasource)
        .flatMap(putDatasource);
  }

  @Override
  public Mono<String> importDashboard(final GrafanaUser user,
                                      final String testId,
                                      final String title,
                                      final Long startDate,
                                      final String dashboard) {
    return this.initDashboard(testId, title, startDate, dashboard)
        .flatMap(this::importDashboard);
  }

  @Override
  public Mono<String> updateDashboard(final String testId, final Long endDate) {
    return this.getDashboard(testId)
        .flatMap(dashboard -> this.updatedDashboard(endDate, dashboard))
        .flatMap(this::setDashboard);
  }

  @Override
  public Mono<String> deleteDashboard(final String testId) {
    return retry(webClient.delete()
        .uri(uriBuilder -> uriBuilder.path("/api/dashboards/uid/{testId}").build(testId))
        .retrieve()
        .bodyToMono(String.class), log);
  }

  private Mono<String> getDashboard(final String testId) {
    return retry(webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/api/dashboards/uid/{testId}").build(testId))
        .retrieve()
        .bodyToMono(String.class)
        .flatMap(this::decapsulateDashboard), log);
  }

  private Mono<String> setDashboard(final String dashboard) {
    return encapsulateSetDashboard(dashboard)
        .flatMap(encapsulated -> retry(webClient.post()
            .uri(uriBuilder -> uriBuilder.path("/api/dashboards/db").build())
            .body(BodyInserters.fromValue(encapsulated))
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(String.class), log));
  }

  private Mono<String> importDashboard(final String dashboard) {
    return encapsulateImportDashboard(dashboard)
        .flatMap(encapsulated -> retry(webClient.post()
            .uri(uriBuilder -> uriBuilder.path("/api/dashboards/import").build())
            .body(BodyInserters.fromValue(encapsulated))
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(String.class), log));
  }

  private Mono<String> encapsulateSetDashboard(final String dashboard) {
    return Mono.fromCallable(() -> {
      final JsonNode dashboardNode = mapper.readTree(dashboard);
      // Dashboard must be encapsulated in another object when updating value
      final ObjectNode setNode = mapper.createObjectNode();
      setNode.set("dashboard", dashboardNode);
      setNode.put("overwrite", false);
      setNode.put("message", this.format.format(new Date()));
      return mapper.writeValueAsString(setNode);
    });
  }

  private Mono<String> decapsulateDashboard(final String dashboardResult) {
    return Mono.fromCallable(() -> {
      final JsonNode dashboardResultNode = mapper.readTree(dashboardResult);
      // Dashboard must be decapsulated when received from grafana server
      return mapper.writeValueAsString(dashboardResultNode.get("dashboard"));
    });
  }

  private Mono<String> initDashboard(final String testId,
                                     final String title,
                                     final Long startDate,
                                     final String dashboard) {
    return Mono.fromCallable(() -> {
      final ObjectNode dashboardNode = ((ObjectNode) mapper.readTree(dashboard));
      // Id must be nullified when importing a dashboard
      dashboardNode.set("id", NullNode.getInstance());
      dashboardNode.put("uid", testId);
      // Ensure title is unique
      dashboardNode.put("title", title);
      dashboardNode.put("version", 1);
      dashboardNode.put("refresh", "1s");
      dashboardNode.put("timezone", "utc");

      final ObjectNode timeNode = ((ObjectNode) dashboardNode.get("time"));
      timeNode.put("from", this.format.format(new Date(startDate)));
      timeNode.put("to", "now");

      final ObjectNode timePickerNode = ((ObjectNode) dashboardNode.get("timepicker"));
      timePickerNode.put("hidden", true);

      final ObjectNode testVariableNode = ((ObjectNode) dashboardNode.get("templating").get("list").get(1));
      testVariableNode.put("query", testId);
      final ObjectNode currentNode = ((ObjectNode) testVariableNode.get("current"));
      currentNode.put("text", testId);
      currentNode.put("value", testId);
      final ObjectNode optionsNode = ((ObjectNode) testVariableNode.get("options").get(0));
      optionsNode.put("text", testId);
      optionsNode.put("value", testId);

      // TODO update datasource with datasourceName
      // TODO update testId requests with the databaseName

      return mapper.writeValueAsString(dashboardNode);
    });
  }

  private Mono<String> updatedDashboard(final Long endDate,
                                        final String dashboard) {
    return Mono.fromCallable(() -> {
      final JsonNode node = mapper.readTree(dashboard);
      final ObjectNode objectNode = ((ObjectNode) node);

      final boolean refresh = endDate == 0L;
      final ObjectNode timeNode = ((ObjectNode) node.get("time"));
      if (refresh) {
        objectNode.put("refresh", "1s");
        timeNode.put("to", "now");
      } else {
        objectNode.put("refresh", false);
        timeNode.put("to", this.format.format(new Date(endDate)));
      }
      return mapper.writeValueAsString(node);
    });
  }

  private Mono<String> encapsulateImportDashboard(final String dashboard) {
    return Mono.fromCallable(() -> {
      final JsonNode dashboardNode = mapper.readTree(dashboard);
      // Dashboard must be encapsulated in another object when importing
      final ObjectNode importNode = mapper.createObjectNode();
      importNode.set("dashboard", dashboardNode);
      importNode.put("overwrite", true);
      importNode.set("inputs", mapper.createArrayNode());
      importNode.put("folderId", 0);
      return mapper.writeValueAsString(importNode);
    });
  }

}
