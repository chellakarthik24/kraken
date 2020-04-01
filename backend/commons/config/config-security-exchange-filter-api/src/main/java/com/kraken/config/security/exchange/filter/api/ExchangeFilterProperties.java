package com.kraken.config.security.exchange.filter.api;

import com.kraken.config.api.KrakenProperties;

import java.util.Optional;

public interface ExchangeFilterProperties extends KrakenProperties {
  ExchangeFilterMode getMode();
  Optional<String> getToken();
}
