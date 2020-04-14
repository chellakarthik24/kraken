package com.kraken.security.decoder.api;

import com.kraken.security.entity.user.KrakenUser;

import java.io.IOException;

@FunctionalInterface
public interface TokenDecoder {
  KrakenUser decode(String token) throws IOException;
}
