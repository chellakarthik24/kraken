package com.kraken.config.security.api;

import com.kraken.config.api.KrakenProperties;

public interface SecurityProperties extends KrakenProperties {

  SecurityClaimsProperties getClaims();

}
