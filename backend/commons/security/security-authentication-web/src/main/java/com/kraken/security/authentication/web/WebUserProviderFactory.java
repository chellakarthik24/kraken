package com.kraken.security.authentication.web;

import com.kraken.security.authentication.api.AuthenticationMode;
import com.kraken.security.authentication.api.UserProviderFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.kraken.security.authentication.api.AuthenticationMode.WEB;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class WebUserProviderFactory implements UserProviderFactory {

  @NonNull WebUserProvider userProvider;

  @Override
  public WebUserProvider create(final String userId) {
    return userProvider;
  }

  @Override
  public AuthenticationMode getMode() {
    return WEB;
  }
}
