package com.kraken.security.authentication.container;

import com.kraken.config.security.container.api.SecurityContainerProperties;
import com.kraken.security.authentication.api.UserProvider;
import com.kraken.security.client.api.SecurityClient;
import com.kraken.security.decoder.api.TokenDecoder;
import com.kraken.security.entity.KrakenToken;
import com.kraken.security.entity.KrakenUser;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContainerUserProvider implements UserProvider {

  SecurityContainerProperties properties;
  TokenDecoder decoder;
  SecurityClient client;
  AtomicReference<KrakenToken> token;

  public ContainerUserProvider(final SecurityContainerProperties properties,
                               final TokenDecoder decoder,
                               final SecurityClient client) {
    this.properties = Objects.requireNonNull(properties);
    this.decoder = Objects.requireNonNull(decoder);
    this.client = Objects.requireNonNull(client);
    this.token = new AtomicReference<>(KrakenToken.builder()
        .accessToken(properties.getAccessToken())
        .refreshToken(properties.getRefreshToken())
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
          if (user.getExpirationTime().minusSeconds(properties.getMinValidity()).isBefore(Instant.now())) {
            return client.refreshToken(token.getRefreshToken()).doOnNext(this.token::set);
          }
          return Mono.just(token);
        }).map(KrakenToken::getAccessToken);
  }

  private Mono<Tuple2<KrakenToken, KrakenUser>> decodeAccessToken(final KrakenToken token) {
    return Mono.fromCallable(() -> Tuples.of(token, decoder.decode(token.getAccessToken())));
  }

}
