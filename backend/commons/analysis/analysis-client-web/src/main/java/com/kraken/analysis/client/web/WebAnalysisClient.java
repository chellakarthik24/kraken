package com.kraken.analysis.client.web;

import com.kraken.analysis.client.api.AnalysisClient;
import com.kraken.analysis.entity.DebugEntry;
import com.kraken.analysis.entity.Result;
import com.kraken.analysis.entity.ResultStatus;
import com.kraken.config.analysis.client.api.AnalysisClientProperties;
import com.kraken.config.api.UrlProperty;
import com.kraken.security.authentication.api.AuthenticationMode;
import com.kraken.security.authentication.api.ExchangeFilterFactory;
import com.kraken.storage.entity.StorageNode;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@AllArgsConstructor(access = PRIVATE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class WebAnalysisClient implements AnalysisClient {

  List<ExchangeFilterFactory> exchangeFilterFactories;
  AnalysisClientProperties properties;
  WebClient webClient;

  public WebAnalysisClient(@NonNull final List<ExchangeFilterFactory> exchangeFilterFactories,
                           @NonNull final AnalysisClientProperties properties) {
    this.exchangeFilterFactories = exchangeFilterFactories;
    this.properties = properties;
    final var filter = exchangeFilterFactories.stream()
        .filter(exchangeFilter -> exchangeFilter.getMode().equals(AuthenticationMode.NOOP))
        .findFirst()
        .orElseThrow()
        .create("");
    this.webClient = WebClient.builder()
        .filter(filter)
        .baseUrl(properties.getUrl())
        .build();
  }

  public WebAnalysisClient withAuthenticationMode(final AuthenticationMode mode, final String userId) {
    final var filter = exchangeFilterFactories.stream()
        .filter(exchangeFilter -> exchangeFilter.getMode().equals(mode))
        .findFirst()
        .orElseThrow()
        .create(userId);
    return new WebAnalysisClient(exchangeFilterFactories, properties, WebClient.builder()
        .filter(filter)
        .baseUrl(properties.getUrl())
        .build());
  }

  public WebAnalysisClient withApplicationId(final String applicationId) {
    return new WebAnalysisClient(exchangeFilterFactories, properties, this.webClient.mutate().defaultHeader("ApplicationId", applicationId).build());
  }

  @Override
  public Mono<StorageNode> create(final Result result) {
    return retry(webClient.post()
        .uri(uriBuilder -> uriBuilder.path("/result")
            .build())
        .body(BodyInserters.fromValue(result))
        .retrieve()
        .bodyToMono(StorageNode.class), log);
  }

  @Override
  public Mono<String> delete(final String resultId) {
    return retry(webClient.delete()
        .uri(uriBuilder -> uriBuilder.path("/result")
            .queryParam("resultId", resultId)
            .build())
        .retrieve()
        .bodyToMono(String.class), log);
  }

  @Override
  public Mono<StorageNode> setStatus(final String resultId, final ResultStatus status) {
    return retry(webClient.post()
        .uri(uriBuilder -> uriBuilder.path("/result/status")
            .pathSegment(status.toString())
            .queryParam("resultId", resultId)
            .build())
        .retrieve()
        .bodyToMono(StorageNode.class), log);
  }

  @Override
  public Mono<DebugEntry> debug(final DebugEntry debug) {
    return retry(webClient.post()
        .uri(uriBuilder -> uriBuilder.path("/result/debug")
            .build())
        .body(BodyInserters.fromValue(debug))
        .retrieve()
        .bodyToMono(DebugEntry.class), log);
  }
}
