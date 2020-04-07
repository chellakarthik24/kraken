package com.kraken.security.client.api;

import com.kraken.config.security.client.api.SecurityClientCredentialsProperties;
import com.kraken.security.entity.KrakenToken;
import reactor.core.publisher.Mono;

public interface SecurityClient {

  Mono<KrakenToken> userLogin(final SecurityClientCredentialsProperties client,
                              final String username,
                              final String password);

  Mono<KrakenToken> clientLogin(final SecurityClientCredentialsProperties client);

  Mono<KrakenToken> exchangeToken(final SecurityClientCredentialsProperties client,
                                  final String accessToken);

  Mono<KrakenToken> refreshToken(final SecurityClientCredentialsProperties client,
                                 final String refreshToken);

}
