package com.kraken.security.authentication.container;

import com.kraken.security.authentication.api.AuthenticationMode;
import com.kraken.security.authentication.api.ExchangeFilterFactory;
import com.kraken.security.authentication.utils.DefaultExchangeFilter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.kraken.security.authentication.api.AuthenticationMode.CONTAINER;

@Slf4j
@Component
final class ContainerExchangeFilterFactory implements ExchangeFilterFactory {

  @NonNull
  ContainerUserProviderFactory userProviderFactory;

  @Override
  public DefaultExchangeFilter create(final String userId) {
    return new DefaultExchangeFilter(userProviderFactory.create(userId));
  }

  @Override
  public AuthenticationMode getMode() {
    return CONTAINER;
  }
}
