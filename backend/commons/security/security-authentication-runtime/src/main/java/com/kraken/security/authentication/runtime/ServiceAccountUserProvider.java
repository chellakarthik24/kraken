package com.kraken.security.authentication.runtime;

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

import javax.swing.text.html.Option;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceAccountUserProvider implements UserProvider {
  private static final Long MIN_VALIDITY = 60L;

  SecurityClientProperties clientProperties;
  TokenDecoder decoder;
  SecurityClient client;
  AtomicReference<Optional<KrakenToken>> token;

  public ServiceAccountUserProvider(final SecurityClientProperties clientProperties,
                                    final TokenDecoder decoder,
                                    final SecurityClient client) {
    this.clientProperties = requireNonNull(clientProperties);
    this.decoder = requireNonNull(decoder);
    this.client = requireNonNull(client);
    this.token = new AtomicReference<>(Optional.empty());
  }

  @Override
  public Mono<KrakenUser> getAuthenticatedUser() {
    return Mono.just(this.token.get().orElseThrow()).flatMap(krakenToken -> Mono.fromCallable(() -> decoder.decode(krakenToken.getAccessToken())));
  }

  @Override
  public Mono<String> getTokenValue() {
    return Mono.just(this.token.get())
        .flatMap(token -> {
          if (token.isEmpty()) {
            return client.clientLogin(clientProperties.getApi()).doOnNext(this::setToken);
          } else {
            return Mono.fromCallable(() -> decoder.decode(token.get().getAccessToken()))
                .flatMap(user -> {
                  if (user.getExpirationTime().minusSeconds(MIN_VALIDITY).isBefore(Instant.now())) {
                    return client.refreshToken(clientProperties.getApi(), token.get().getRefreshToken()).doOnNext(this::setToken);
                  }
                  return Mono.just(token.get());
                });
          }
        }).map(KrakenToken::getAccessToken);
  }

  private void setToken(final KrakenToken token) {
    this.token.set(Optional.of(token));
  }

}
