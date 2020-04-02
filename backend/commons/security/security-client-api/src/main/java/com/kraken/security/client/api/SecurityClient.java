package com.kraken.security.client.api;

import com.kraken.security.entity.KrakenToken;
import reactor.core.publisher.Mono;

public interface SecurityClient {

  Mono<KrakenToken> userLogin(String username, String password);

  Mono<KrakenToken> exchangeToken(String accessToken);

  Mono<KrakenToken> refreshToken(String refreshToken);

}
