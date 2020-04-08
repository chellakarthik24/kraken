package com.kraken.security.authentication.web;

import com.kraken.security.authentication.api.AuthenticationMode;
import com.kraken.security.authentication.api.ExchangeFilterFactory;
import com.kraken.security.authentication.utils.DefaultExchangeFilter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.kraken.security.authentication.api.AuthenticationMode.WEB;

@Slf4j
@Component
final class WebExchangeFilterFactory implements ExchangeFilterFactory {

  @NonNull
  WebUserProviderFactory userProviderFactory;

  @Override
  public DefaultExchangeFilter create(String userId) {
    return new DefaultExchangeFilter(userProviderFactory.create(userId));
  }

  @Override
  public AuthenticationMode getMode() {
    return WEB;
  }
}
