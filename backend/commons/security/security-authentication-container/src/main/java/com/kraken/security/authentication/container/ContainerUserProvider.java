package com.kraken.security.authentication.container;

import com.kraken.config.security.client.api.SecurityClientProperties;
import com.kraken.config.security.container.api.SecurityContainerProperties;
import com.kraken.security.authentication.api.AuthenticationMode;
import com.kraken.security.authentication.utils.AtomicUserProvider;
import com.kraken.security.client.api.SecurityClient;
import com.kraken.security.decoder.api.TokenDecoder;
import com.kraken.security.entity.KrakenToken;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.kraken.security.authentication.api.AuthenticationMode.CONTAINER;
import static java.util.Objects.requireNonNull;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class ContainerUserProvider extends AtomicUserProvider {

  SecurityClientProperties clientProperties;
  SecurityContainerProperties containerProperties;
  SecurityClient client;

  public ContainerUserProvider(final SecurityClientProperties clientProperties,
                               final SecurityContainerProperties containerProperties,
                               final TokenDecoder decoder,
                               final SecurityClient client) {
    super(decoder, containerProperties.getMinValidity());
    this.clientProperties = requireNonNull(clientProperties);
    this.containerProperties = requireNonNull(containerProperties);
    this.client = requireNonNull(client);
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
