package com.kraken.security.entity.owner;

import com.kraken.security.entity.owner.PublicOwner;
import com.kraken.tests.utils.TestUtils;
import org.junit.Test;

public class PublicOwnerTest {

  public static final PublicOwner PUBLIC_OWNER = PublicOwner.builder()
      .build();

  @Test
  public void shouldPassEquals() {
    TestUtils.shouldPassEquals(PUBLIC_OWNER.getClass());
  }

  @Test
  public void shouldPassNPE() {
    TestUtils.shouldPassNPE(PUBLIC_OWNER.getClass());
  }

  @Test
  public void shouldPassToString() {
    TestUtils.shouldPassToString(PUBLIC_OWNER);
  }

}
