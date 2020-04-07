package com.kraken.security.authentication.container;

import com.kraken.config.security.client.api.SecurityClientProperties;
import com.kraken.config.security.container.api.SecurityContainerProperties;
import com.kraken.security.authentication.api.UserProvider;
import com.kraken.security.client.api.SecurityClient;
import com.kraken.security.decoder.api.TokenDecoder;
import com.kraken.security.entity.KrakenToken;
import com.kraken.security.entity.KrakenUser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContainerUserProvider implements UserProvider {

  SecurityClientProperties clientProperties;
  SecurityContainerProperties containerProperties;
  TokenDecoder decoder;
  SecurityClient client;
  AtomicReference<KrakenToken> token;

  public ContainerUserProvider(final SecurityClientProperties clientProperties,
                               final SecurityContainerProperties containerProperties,
                               final TokenDecoder decoder,
                               final SecurityClient client) {
    this.clientProperties = requireNonNull(clientProperties);
    this.containerProperties = requireNonNull(containerProperties);
    this.decoder = requireNonNull(decoder);
    this.client = requireNonNull(client);
    this.token = new AtomicReference<>(KrakenToken.builder()
        .accessToken(containerProperties.getAccessToken())
        .refreshToken(containerProperties.getRefreshToken())
        .build());
  }

  @Override
  public Mono<KrakenUser> getAuthenticatedUser() {
    return Mono.just(this.token.get()).flatMap(this::decodeAccessToken).map(Tuple2::getT2);
  }

  @Override
  public Mono<String> getTokenValue() {
    return Mono.just(this.token.get())
        .flatMap(this::decodeAccessToken)
        .flatMap(t2 -> {
          final var token = t2.getT1();
          final var user = t2.getT2();
          if (user.getExpirationTime().minusSeconds(containerProperties.getMinValidity()).isBefore(Instant.now())) {
            return client.refreshToken(clientProperties.getContainer(), token.getRefreshToken()).doOnNext(this.token::set);
          }
          return Mono.just(token);
        }).map(KrakenToken::getAccessToken);
  }

  private Mono<Tuple2<KrakenToken, KrakenUser>> decodeAccessToken(final KrakenToken token) {
    return Mono.fromCallable(() -> Tuples.of(token, decoder.decode(token.getAccessToken())));
  }

}
