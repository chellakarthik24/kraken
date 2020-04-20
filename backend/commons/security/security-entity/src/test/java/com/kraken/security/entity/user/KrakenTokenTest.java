package com.kraken.security.entity.user;

import com.kraken.tests.utils.TestUtils;
import org.junit.Test;

public class KrakenTokenTest {

  public static final KrakenToken KRAKEN_TOKEN = KrakenToken.builder()
      .accessToken("accessToken")
      .refreshToken("refreshToken")
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
