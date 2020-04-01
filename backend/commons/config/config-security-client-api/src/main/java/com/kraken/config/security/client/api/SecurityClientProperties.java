package com.kraken.config.security.client.api;

import com.kraken.config.api.UrlProperty;

public interface SecurityClientProperties extends UrlProperty {

  String getId();

  String getSecret();
}
