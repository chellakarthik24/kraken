package com.kraken.config.security.client.api;

import com.kraken.config.api.UrlProperty;

public interface SecurityClientProperties extends UrlProperty {

  String getWebId();

  String getApiId();

  String getRealm();
}
