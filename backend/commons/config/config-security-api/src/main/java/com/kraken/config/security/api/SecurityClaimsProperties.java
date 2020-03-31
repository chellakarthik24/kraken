package com.kraken.config.security.api;

import com.kraken.config.api.KrakenProperties;

public interface SecurityClaimsProperties extends KrakenProperties {

  String getGroups();

  String getCurrentGroup();

  String getUsername();

}
