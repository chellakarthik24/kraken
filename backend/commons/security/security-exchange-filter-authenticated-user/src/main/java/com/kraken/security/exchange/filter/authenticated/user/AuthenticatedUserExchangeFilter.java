package com.kraken.security.exchange.filter.authenticated.user;

import com.kraken.security.authentication.api.AuthenticatedUserProvider;
import com.kraken.security.exchange.filter.api.KrakenExchangeFilterFunction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class AuthenticatedUserExchangeFilter implements KrakenExchangeFilterFunction {

  @NonNull AuthenticatedUserProvider userProvider;

  @Override
  public Mono<ClientResponse> filter(final ClientRequest request, final ExchangeFunction next) {
    return userProvider.getTokenValue().map(token -> ClientRequest.from(request)
        .headers(headers -> headers.setBearerAuth(token))
        .build())
        .flatMap(next::exchange);
  }
}
