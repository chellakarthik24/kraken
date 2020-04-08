package com.kraken.security.authentication.client.spring;

import com.kraken.config.api.UrlProperty;
import com.kraken.security.authentication.api.AuthenticationMode;
import com.kraken.security.authentication.api.ExchangeFilter;
import com.kraken.security.authentication.api.ExchangeFilterFactory;
import com.kraken.security.authentication.client.api.AuthenticatedClient;
import com.kraken.security.authentication.client.api.AuthenticatedClientFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.kraken.security.authentication.api.AuthenticationMode.IMPERSONATE;
import static com.kraken.security.authentication.api.AuthenticationMode.NOOP;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractAuthenticatedClientFactory<T extends AuthenticatedClient> implements AuthenticatedClientFactory<T> {

  @NonNull List<ExchangeFilterFactory> exchangeFilterFactories;
  @NonNull UrlProperty property;

  @Override
  public T create() {
    return this.create(NOOP);
  }

  @Override
  public T create(AuthenticationMode mode) {
    checkArgument(!mode.equals(IMPERSONATE), "The user id is required for the IMPERSONATE authentication mode");
    return this.create(mode, "");
  }

  @Override
  public T create(AuthenticationMode mode, String userId) {
    final var filter = exchangeFilterFactories.stream()
        .filter(exchangeFilter -> exchangeFilter.getMode().equals(mode))
        .findFirst()
        .orElseThrow()
        .create(userId);
    return this.create(WebClient.builder().filter(filter).baseUrl(property.getUrl()));
  }

  protected abstract T create(WebClient.Builder webClientBuilder);
}
