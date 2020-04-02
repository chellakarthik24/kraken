package com.kraken.security.entity;

import com.google.common.collect.ImmutableList;
import com.kraken.test.utils.TestUtils;
import org.junit.Test;

import java.time.Instant;

public class KrakenUserTest {

  public static final KrakenUser KRAKEN_USER = KrakenUser.builder()
      .issuedAt(Instant.EPOCH)
      .expirationTime(Instant.EPOCH.plusMillis(1))
      .userId("userId")
      .username("username")
      .roles(ImmutableList.of(KrakenRole.USER))
      .groups(ImmutableList.of("/default-kraken"))
      .currentGroup("/default-kraken")
      .build();

  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(KRAKEN_USER.getClass());
  }

  @Test
  public void shouldPassToString() {
    TestUtils.shouldPassToString(KRAKEN_USER);
  }

}
