package com.kraken.config.security.jwt.api;

import com.kraken.config.api.KrakenProperties;

public interface JwtProperties extends KrakenProperties {

  JwtClaimsProperties getClaims();

}
