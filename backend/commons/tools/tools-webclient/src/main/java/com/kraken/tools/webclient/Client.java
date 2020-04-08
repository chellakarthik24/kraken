package com.kraken.tools.webclient;

import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public interface Client {

  int NUM_RETRIES = 5;
  Duration FIRST_BACKOFF = Duration.ofMillis(100);

  default <T> Mono<T> retry(Mono<T> mono, Logger log) {
    return mono.retryBackoff(NUM_RETRIES, FIRST_BACKOFF)
        .doOnError(throwable -> throwable.getCause() instanceof WebClientResponseException, throwable -> {
          log.info(((WebClientResponseException) throwable.getCause()).getResponseBodyAsString());
        });
  }

  default <T> Flux<T> retry(Flux<T> flux, Logger log) {
    return flux.retryBackoff(NUM_RETRIES, FIRST_BACKOFF)
        .doOnError(throwable -> throwable.getCause() instanceof WebClientResponseException, throwable -> {
          log.info(((WebClientResponseException) throwable.getCause()).getResponseBodyAsString());
        });
  }
}
