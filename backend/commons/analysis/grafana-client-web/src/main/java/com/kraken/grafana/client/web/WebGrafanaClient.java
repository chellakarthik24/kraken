package com.kraken.grafana.client.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.kraken.config.grafana.api.GrafanaProperties;
import com.kraken.grafana.client.api.GrafanaClient;
import com.kraken.grafana.client.api.GrafanaUser;
import com.kraken.influxdb.client.api.InfluxDBUser;
import com.kraken.security.entity.token.KrakenTokenUser;
import com.kraken.tools.unique.id.IdGenerator;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Component
final class WebGrafanaClient implements GrafanaClient {

  WebClient webClient;
  ObjectMapper mapper;
  SimpleDateFormat format;
  IdGenerator idGenerator;
  GrafanaProperties properties;


  WebGrafanaClient(@NonNull final GrafanaProperties grafanaProperties,
                   @NonNull final ObjectMapper mapper,
                   @NonNull final IdGenerator idGenerator) {
    final var credentials = grafanaProperties.getUser() + ":" + grafanaProperties.getPassword();
    final var encoded = Base64.getEncoder().encodeToString(credentials.getBytes(UTF_8));
    this.webClient = WebClient
        .builder()
        .baseUrl(grafanaProperties.getUrl())
        .defaultHeader("Authorization", "Basic " + encoded)
        .build();
    this.mapper = mapper;
    this.idGenerator = idGenerator;
    //  2019-03-22T10:01:00.000Z
    this.format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    format.setTimeZone(TimeZone.getTimeZone("UTC"));

    this.properties = grafanaProperties;
  }

  @Override
  public Mono<String> importDashboard(final GrafanaUser user,
                                      final String testId,
                                      final String title,
                                      final Long startDate,
                                      final String dashboard) {
    return this.initDashboard(testId, title, startDate, dashboard)
        .flatMap(this::importDashboard);
    // TODO Dashboard permission! => Add user en paramètre

//    POST /api/dashboards/id/1/permissions
//    Accept: application/json
//    Content-Type: application/json
//    Authorization: Bearer eyJrIjoiT0tTcG1pUlY2RnVKZTFVaDFsNFZXdE9ZWmNrMkZYbk
//
//    {
//      "items": [
//      {
//        "userId": 11,
//          "permission": 4
//      }
//  ]
//    }

  }

  @Override
  public Mono<String> updateDashboard(final String testId, final Long endDate) {
    return this.getDashboard(testId)
        .flatMap(dashboard -> this.updatedDashboard(endDate, dashboard))
        .flatMap(this::setDashboard);
  }

  @Override
  public Mono<GrafanaUser> createUser(final KrakenTokenUser tokenUser,
                                      final InfluxDBUser dbUser) {
    final var password = idGenerator.generate();

    final var createUser = retry(this.webClient.post()
        .uri(uriBuilder -> uriBuilder.path("/api/admin/users").build())
        .body(BodyInserters.fromValue(CreateGrafanaUserRequest.builder()
            .name(tokenUser.getUsername())
            .email(tokenUser.getEmail())
            .login(tokenUser.getUsername())
            .password(password)
            .build()))
        .retrieve()
        .bodyToMono(CreateGrafanaUserResponse.class), log);


    // TODO Datasource (prendre le influxDDUser en paramètre ?)
    // TODO injecter les properties avec l'url d'influxdb !!!
//    http://localhost:8086
// user_i05tk8x65h
    // Faut garder le orgId du user ???
// request {"id":3,"orgId":3,"name":"InfluxDB","type":"influxdb","typeLogoUrl":"","access":"proxy","url":"http://localhost:8086","password":"","user":"user_obohzuo5gr","database":"db_obohzuo5gr","basicAuth":false,"basicAuthUser":"user_obohzuo5gr","basicAuthPassword":"","withCredentials":false,"isDefault":true,"jsonData":{"httpMode":"POST"},"secureJsonFields":{},"version":1,"readOnly":false,"secureJsonData":{"password":"pwd_fhyjkgimpx","basicAuthPassword":"pwd_fhyjkgimpx"}}
// response {"datasource":{"id":3,"orgId":3,"name":"InfluxDB","type":"influxdb","typeLogoUrl":"","access":"proxy","url":"http://localhost:8086","password":"","user":"user_obohzuo5gr","database":"db_obohzuo5gr","basicAuth":false,"basicAuthUser":"user_obohzuo5gr","basicAuthPassword":"","withCredentials":false,"isDefault":true,"jsonData":{"httpMode":"POST"},"secureJsonFields":{"basicAuthPassword":true,"password":true},"version":2,"readOnly":false},"id":3,"message":"Datasource updated","name":"InfluxDB"}


//    POST /api/datasources HTTP/1.1
//    Accept: application/json
//    Content-Type: application/json
//    Authorization: Bearer eyJrIjoiT0tTcG1pUlY2RnVKZTFVaDFsNFZXdE9ZWmNrMkZYbk
//
//    {
//      "name":"test_datasource",
//        "type":"graphite",
//        "url":"http://mydatasource.com",
//        "access":"proxy",
//        "basicAuth":false
//    }
//    Example Graphite Response:
//
//    HTTP/1.1 200
//    Content-Type: application/json
//
//    {
//      "datasource": {
//      "id": 1,
//          "orgId": 1,
//          "name": "test_datasource",
//          "type": "graphite",
//          "typeLogoUrl": "",
//          "access": "proxy",
//          "url": "http://mydatasource.com",
//          "password": "",
//          "user": "",
//          "database": "",
//          "basicAuth": false,
//          "basicAuthUser": "",
//          "basicAuthPassword": "",
//          "withCredentials": false,
//          "isDefault": false,
//          "jsonData": {},
//      "secureJsonFields": {},
//      "version": 1,
//          "readOnly": false
//    },
//      "id": 1,
//        "message": "Datasource added",
//        "name": "test_datasource"
//    }
    return createUser.map(response -> GrafanaUser.builder()
        .id(response.getId().toString())
        .username(tokenUser.getUsername())
        .password(password)
        .email(tokenUser.getEmail())
        .build()
    );
  }

  public Mono<Void> createDatasource(final GrafanaUser grafanaUser,
                                     final InfluxDBUser dbUser){
    final var datasourceId = idGenerator.generate();
    final var credentials = grafanaUser.getUsername() + ":" + grafanaUser.getPassword();
    final var encoded = Base64.getEncoder().encodeToString(credentials.getBytes(UTF_8));
    final var webClient = WebClient
        .builder()
        .baseUrl(this.properties.getUrl())
        .defaultHeader("Authorization", "Basic " + encoded)
        .build();

    // CREATE /grafana/api/datasources
//    POST {"name":"InfluxDB-1","type":"influxdb","access":"proxy","isDefault":false}
// Response {"datasource":{"id":7,"orgId":4,"name":"InfluxDB-1","type":"influxdb","typeLogoUrl":"","access":"proxy","url":"","password":"","user":"","database":"","basicAuth":false,"basicAuthUser":"","basicAuthPassword":"","withCredentials":false,"isDefault":false,"jsonData":{},"secureJsonFields":{},"version":1,"readOnly":false},"id":7,"message":"Datasource added","name":"InfluxDB-1"}
// TODO Extract the datascourceJson => update => send

    // UPDATE dans la foulée
//    {
//      "id": 6,
//        "orgId": 4,
//        "name": "InfluxDB",
//        "type": "influxdb",
//        "typeLogoUrl": "",
//        "access": "proxy",
//        "url": "",
//        "password": "",
//        "user": "user_i05tk8x65h",
//        "database": "db_i05tk8x65h",
//        "basicAuth": false,
//        "basicAuthUser": "",
//        "basicAuthPassword": "",
//        "withCredentials": false,
//        "isDefault": false,
//        "jsonData": {
//      "httpMode": "POST"
//    },
//      "secureJsonFields": {},
//      "version": 1,
//        "readOnly": false,
//        "secureJsonData": {
//      "password": "pwd_wjstgq9xpc"
//    }
//    }

    final var createDatasource = retry(webClient.put()
        .uri(uriBuilder -> uriBuilder.path("/api/datasources/{id}").build(datasourceId))
        .body(BodyInserters.fromValue(CreateGrafanaDatasourceRequest.builder()
            .id(datasourceId)
            .orgId(grafanaUser.getId())
            .name(grafanaUser.getUsername())
            .type("influxdb").access("proxy")
            .url("http://localhost:8086")
            .password("")
            .typeLogoUrl("")
            .basicAuth(false)
            .basicAuthUser("")
            .basicAuthPassword("")
            .user(dbUser.getUsername())
            .database(dbUser.getDatabase())
            .withCredentials(false)
            .isDefault(true)
            .jsonData(ImmutableMap.of("httpMode", "POST"))
            .readOnly(true)
            .secureJsonData(ImmutableMap.of("password", dbUser.getPassword(),
                "basicAuthPassword", dbUser.getPassword()))
            .build()))
        .retrieve()
        .bodyToMono(String.class), log);

    return createDatasource.then();
  }

  @Override
  public Mono<Void> deleteUser(final GrafanaUser user) {
    final var deleteUser = retry(webClient.delete()
        .uri(uriBuilder -> uriBuilder.path("/api/admin/users/{id}").build(user.getId()))
        .retrieve()
        .bodyToMono(Void.class), log);

    // TODO Delete datasource
    return deleteUser;
  }

  @Override
  public Mono<String> login(GrafanaUser user) {
    return null;
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
    // TODO Use datasource id for the current user
//    TODO Return dashboard id!
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
