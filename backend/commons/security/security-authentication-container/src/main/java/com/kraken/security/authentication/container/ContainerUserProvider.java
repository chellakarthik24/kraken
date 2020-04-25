package com.kraken.security.authentication.container;

import com.google.common.annotations.VisibleForTesting;
import com.kraken.config.security.client.api.SecurityClientProperties;
import com.kraken.config.security.container.api.SecurityContainerProperties;
import com.kraken.security.authentication.utils.AtomicUserProvider;
import com.kraken.security.client.api.SecurityClient;
import com.kraken.security.decoder.api.TokenDecoder;
import com.kraken.security.entity.user.KrakenToken;
import com.kraken.security.entity.user.KrakenUser;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ContainerUserProvider extends AtomicUserProvider {

  SecurityClientProperties clientProperties;
  SecurityContainerProperties containerProperties;
  SecurityClient client;

  public ContainerUserProvider(@NonNull final SecurityClientProperties clientProperties,
                               @NonNull final SecurityContainerProperties containerProperties,
                               @NonNull final TokenDecoder decoder,
                               @NonNull final SecurityClient client) {
    super(decoder, containerProperties.getMinValidity());
    this.clientProperties = clientProperties;
    this.containerProperties = containerProperties;
    this.client = client;
//    this.periodicRefresh(aLong -> super.getTokenValue());
  }


  @VisibleForTesting
  Flux<String> periodicRefresh(final Function<Long, Mono<String>> refresh) {
    // Periodic refresh to keep the token alive
    // TODO Trop chiant => ajouter les "expires_in":300,"refresh_expires_in":1800 au KrakenToken et les passer en env du container

//    {"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJZb19LT2IzeUZncGlzM05tT1F2OFE0N2ZJQlltbkpsZEtlRE1LQ1lBQThjIn0.eyJleHAiOjE1ODc4NDExNDUsImlhdCI6MTU4Nzg0MDg0NSwianRpIjoiMWNlMDQ3MDAtNDYzYy00ODg4LWJhZjktMjg1YzYxN2YxZGI2IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MDgwL2F1dGgvcmVhbG1zL2tyYWtlbiIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIyZTQ0ZmZhZS0xMTFjLTRmNTktYWUyYi02NTAwMGRlNmY3YjciLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJrcmFrZW4td2ViIiwic2Vzc2lvbl9zdGF0ZSI6ImJmZGE0ODU1LWE0MzYtNGVlZS1iNTdiLTg5NjBlZDlhZWJiZCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsIlVTRVIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJjdXJyZW50X2dyb3VwIjoiL2RlZmF1bHQta3Jha2VuIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJLcmFrZW4gVXNlciIsInByZWZlcnJlZF91c2VybmFtZSI6ImtyYWtlbi11c2VyIiwiZ2l2ZW5fbmFtZSI6IktyYWtlbiIsImZhbWlseV9uYW1lIjoiVXNlciIsImVtYWlsIjoiY29udGFjdEBvY3RvcGVyZi5jb20iLCJ1c2VyX2dyb3VwcyI6WyIvZGVmYXVsdC1rcmFrZW4iXX0.UX_565p-LD28PhtmG_pPR11-lqaNbpZs49_kKxSTjRqvp9gi_AUHywG-NbhJAE0HKHAdBEZx7AkKv33aTGN3TwLgW9AhjfHwBnBO1A4EZtOxewdmIb6ZnmKIFH0zowMNxHBFLxbR3wwLG_wucWJIN4sm10rLEgz3QQ9Hs-KGh_iL3fJKH44tCB_xtZVIV-U8yQuRmriLxvsJoG44q_gqY0yrv53pXtn8SkCnsz8T5VIs6mhaz0SX8hopdsV6m_pMfE1Gxl39KAw001BTp-XKn3iWslvvbyuF-3mqkipqqf0jfixcGAwLbPmI0sSCVyDGcWVmfl-_0hDaSAkLKZbdFw","expires_in":300,"refresh_expires_in":1800,"refresh_token":"eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4MGY1ZTAyMS04M2MxLTQzNzUtOWE4YS1kNTFlNzI4ZDQ5MWQifQ.eyJleHAiOjE1ODc4NDI2NDUsImlhdCI6MTU4Nzg0MDg0NSwianRpIjoiYjdkYzljMzYtYTE3YS00NTAxLTgzYTktNjcyZmQxNWE0N2VjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MDgwL2F1dGgvcmVhbG1zL2tyYWtlbiIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6OTA4MC9hdXRoL3JlYWxtcy9rcmFrZW4iLCJzdWIiOiIyZTQ0ZmZhZS0xMTFjLTRmNTktYWUyYi02NTAwMGRlNmY3YjciLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoia3Jha2VuLXdlYiIsInNlc3Npb25fc3RhdGUiOiJiZmRhNDg1NS1hNDM2LTRlZWUtYjU3Yi04OTYwZWQ5YWViYmQiLCJzY29wZSI6ImVtYWlsIHByb2ZpbGUifQ.lC7T8WpexR7pGtp-odJbiJqwSWkUAZZTf_O_kUfUwL8","token_type":"bearer","not-before-policy":1586532023,"session_state":"bfda4855-a436-4eee-b57b-8960ed9aebbd","scope":"email profile"}

    return Mono.just(containerProperties.getRefreshToken())
        .flatMap(refreshToken -> Mono.fromCallable(() -> decoder.decode(refreshToken)))
        .flatMapMany(user -> {
          final var refreshInterval = Duration.between(user.getIssuedAt(), user.getExpirationTime());
          System.out.println(refreshInterval.toSeconds());
          return Flux.interval(Duration.ZERO, refreshInterval.minus(refreshInterval.dividedBy(10L)));
        })
        .flatMap(refresh);
//        .retryBackoff(Integer.MAX_VALUE, Duration.ofSeconds(5))
//        .onErrorContinue((throwable, o) -> log.error("Failed to refresh container token " + o, throwable))
  }

  @Override
  protected Mono<KrakenToken> newToken() {
    return Mono.just(KrakenToken.builder()
        .accessToken(containerProperties.getAccessToken())
        .refreshToken(containerProperties.getRefreshToken())
        .build()).flatMap(this::refreshToken);
  }

  @Override
  protected Mono<KrakenToken> refreshToken(KrakenToken token) {
    return client.refreshToken(clientProperties.getContainer(), token.getRefreshToken());
  }

}
