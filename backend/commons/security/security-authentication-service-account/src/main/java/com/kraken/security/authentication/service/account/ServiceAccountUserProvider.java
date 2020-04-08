package com.kraken.security.authentication.service.account;

import com.kraken.config.security.client.api.SecurityClientProperties;
import com.kraken.config.security.container.api.SecurityContainerProperties;
import com.kraken.security.authentication.api.AuthenticationMode;
import com.kraken.security.authentication.api.UserProvider;
import com.kraken.security.authentication.utils.AtomicUserProvider;
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

import static com.kraken.security.authentication.api.AuthenticationMode.SERVICE_ACCOUNT;
import static java.util.Objects.requireNonNull;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class ServiceAccountUserProvider extends AtomicUserProvider {

  SecurityClientProperties clientProperties;
  SecurityClient client;

  public ServiceAccountUserProvider(final SecurityClientProperties clientProperties,
                                    final TokenDecoder decoder,
                                    final SecurityClient client) {
    super(decoder, 60L);
    this.clientProperties = requireNonNull(clientProperties);
    this.client = requireNonNull(client);
  }

  @Override
  protected Mono<KrakenToken> newToken() {
    return client.clientLogin(clientProperties.getApi());
  }

  @Override
  protected Mono<KrakenToken> refreshToken(KrakenToken token) {
    return client.refreshToken(clientProperties.getApi(), token.getRefreshToken());
  }

}
