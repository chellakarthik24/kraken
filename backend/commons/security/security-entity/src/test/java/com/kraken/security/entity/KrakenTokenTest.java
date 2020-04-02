package com.kraken.security.entity;

import com.kraken.test.utils.TestUtils;
import org.junit.Test;

import static com.kraken.test.utils.TestUtils.shouldPassAll;

public class KrakenTokenTest {

  public static final KrakenToken KRAKEN_TOKEN = KrakenToken.builder()
      .accessToken("accessToken")
      .expiresIn(42)
      .refreshExpiresIn(1337)
      .refreshToken("refreshToken")
      .tokenType("tokenType")
      .sessionState("sessionState")
      .scope("scope")
      .build();


  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(KRAKEN_TOKEN.getClass());
  }

  @Test
  public void shouldPassToString() {
    TestUtils.shouldPassToString(KRAKEN_TOKEN);
  }

}
