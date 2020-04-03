package com.kraken.analysis.client.web;

import com.kraken.analysis.client.api.AnalysisClient;
import com.kraken.analysis.entity.DebugEntry;
import com.kraken.analysis.entity.Result;
import com.kraken.analysis.entity.ResultStatus;
import com.kraken.config.analysis.client.api.AnalysisClientProperties;
import com.kraken.security.exchange.filter.api.ExchangeFilter;
import com.kraken.storage.entity.StorageNode;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class WebAnalysisClient implements AnalysisClient {
  WebClient webClient;

  WebAnalysisClient(final AnalysisClientProperties properties,
                    final ExchangeFilter exchangeFilter) {
    this.webClient = WebClient
        .builder()
        .baseUrl(properties.getUrl())
        .filter(exchangeFilter)
        .build();
  }

  @Override
  public Mono<StorageNode> create(final Result result) {
    return webClient.post()
        .uri(uriBuilder -> uriBuilder.path("/result")
            .build())
        .body(BodyInserters.fromValue(result))
        .retrieve()
        .bodyToMono(StorageNode.class);
  }

  @Override
  public Mono<String> delete(final String resultId) {
    return webClient.delete()
        .uri(uriBuilder -> uriBuilder.path("/result")
            .queryParam("resultId", resultId)
            .build())
        .retrieve()
        .bodyToMono(String.class);
  }

  @Override
  public Mono<StorageNode> setStatus(final String resultId, final ResultStatus status) {
    return webClient.post()
        .uri(uriBuilder -> uriBuilder.path("/result/status")
            .pathSegment(status.toString())
            .queryParam("resultId", resultId)
            .build())
        .retrieve()
        .bodyToMono(StorageNode.class);
  }

  @Override
  public Mono<DebugEntry> debug(final DebugEntry debug) {
    return webClient.post()
        .uri(uriBuilder -> uriBuilder.path("/result/debug")
            .build())
        .body(BodyInserters.fromValue(debug))
        .retrieve()
        .bodyToMono(DebugEntry.class);
  }
}
