package com.kraken.influxdb.client.web;

import com.google.common.base.Charsets;
import com.kraken.config.influxdb.api.InfluxDBProperties;
import com.kraken.influxdb.client.api.InfluxDBClient;
import com.kraken.influxdb.client.api.InfluxDBUser;
import com.kraken.tools.unique.id.IdGenerator;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Component
final class WebInfluxDBClient implements InfluxDBClient {

  IdGenerator idGenerator;
  WebClient client;
  InfluxDBProperties properties;

  WebInfluxDBClient(@NonNull final InfluxDBProperties properties,
                    @NonNull final IdGenerator idGenerator) {
    super();
    this.idGenerator = idGenerator;
    final var credentials = properties.getUser() + ":" + properties.getPassword();
    final var encoded = Base64.getEncoder().encodeToString(credentials.getBytes(Charsets.UTF_8));
    this.client = WebClient
        .builder()
        .baseUrl(properties.getUrl())
        .defaultHeader("Authorization", "Basic " + encoded)
        .build();
    this.properties = requireNonNull(properties);
  }

  @Override
  public Mono<String> deleteSeries(final String database, final String testId) {
    return retry(client.post()
        .uri(uri -> uri.path("/query").queryParam("db", database).build())
        .body(fromFormData("q", format("DROP SERIES FROM /.*/ WHERE test = '%s'", testId)))
        .retrieve()
        .bodyToMono(String.class), log);
  }

  @Override
  public Mono<InfluxDBUser> createUser() {
    final var id = idGenerator.generate();
    final var user = InfluxDBUser.builder()
        .username("user_" + id)
        .database("db_" + id)
        .password("pwd_" + idGenerator.generate())
        .build();

    final var createUser = retry(client.post()
        .uri(uri -> uri.path("/query").build())
        .body(fromFormData("q", format("CREATE USER %s WITH PASSWORD '%s'", user.getUsername(), user.getPassword())))
        .retrieve()
        .bodyToMono(String.class), log);

    final var createDB = retry(client.post()
        .uri(uri -> uri.path("/query").build())
        .body(fromFormData("q", format("CREATE DATABASE %s", user.getDatabase())))
        .retrieve()
        .bodyToMono(String.class), log);

    final var grantPrivileges = retry(client.post()
        .uri(uri -> uri.path("/query").build())
        .body(fromFormData("q", format("GRANT ALL ON %s TO %s", user.getDatabase(), user.getUsername())))
        .retrieve()
        .bodyToMono(String.class), log);

    return createUser.then(createDB).then(grantPrivileges).map(s -> user);
  }

  @Override
  public Mono<Void> deleteUser(InfluxDBUser user) {
    final var dropDB = retry(client.post()
        .uri(uri -> uri.path("/query").build())
        .body(fromFormData("q", format("DROP DATABASE %s", user.getDatabase())))
        .retrieve()
        .bodyToMono(String.class), log);

    final var dropUser = retry(client.post()
        .uri(uri -> uri.path("/query").build())
        .body(fromFormData("q", format("DROP USER %s", user.getUsername())))
        .retrieve()
        .bodyToMono(Void.class), log);

    return dropDB.then(dropUser);
  }
}
