package com.kraken.security.authentication.utils;

import com.kraken.security.authentication.api.ExchangeFilter;
import com.kraken.security.authentication.api.UserProvider;
import com.kraken.security.decoder.api.TokenDecoder;
import com.kraken.security.entity.KrakenToken;
import com.kraken.security.entity.KrakenUser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class AtomicUserProvider implements UserProvider {

  TokenDecoder decoder;
  AtomicReference<Optional<KrakenToken>> token;
  Long minValidity;

  public AtomicUserProvider(final TokenDecoder decoder,
                            final Long minValidity) {
    this.decoder = requireNonNull(decoder);
    this.minValidity = requireNonNull(minValidity);
    this.token = new AtomicReference<>(empty());
  }

  @Override
  public Mono<KrakenUser> getAuthenticatedUser() {
    return this.getTokenValue().flatMap(token -> Mono.fromCallable(() -> decoder.decode(token)));
  }

  @Override
  public Mono<String> getTokenValue() {
    return Mono.just(this.token.get())
        .flatMap(token -> {
          if (token.isEmpty()) {
            return this.newToken().doOnNext(this::setToken);
          } else {
            return Mono
                .fromCallable(() -> decoder.decode(token.get().getAccessToken()))
                .flatMap(user -> {
                  if (user.getExpirationTime().minusSeconds(minValidity).isBefore(Instant.now())) {
                    return refreshToken(token.get()).doOnNext(this::setToken);
                  }
                  return Mono.just(token.get());
                });
          }
        }).map(KrakenToken::getAccessToken);
  }

  protected abstract Mono<KrakenToken> newToken();

  protected abstract Mono<KrakenToken> refreshToken(KrakenToken token);

  private void setToken(final KrakenToken token) {
    this.token.set(Optional.of(token));
  }
}
