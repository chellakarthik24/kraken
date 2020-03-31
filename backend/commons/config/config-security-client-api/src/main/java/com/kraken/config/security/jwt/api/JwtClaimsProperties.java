package com.kraken.config.security.jwt.api;

import com.kraken.config.api.KrakenProperties;

public interface JwtClaimsProperties extends KrakenProperties {

  String getGroups();

  String getCurrentGroup();

  String getUsername();

}
