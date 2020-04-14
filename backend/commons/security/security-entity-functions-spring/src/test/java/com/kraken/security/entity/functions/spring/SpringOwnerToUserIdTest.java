package com.kraken.security.entity.functions.spring;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static com.kraken.security.entity.owner.ApplicationOwnerTest.APPLICATION_OWNER;
import static com.kraken.security.entity.owner.PublicOwnerTest.PUBLIC_OWNER;
import static com.kraken.security.entity.owner.UserOwnerTest.USER_OWNER;

public class SpringOwnerToUserIdTest {

  SpringOwnerToUserId toApplicationId;

  @Before
  public void setUp() {
    toApplicationId = new SpringOwnerToUserId();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldConvertApplicationOwnerFail() {
    toApplicationId.apply(APPLICATION_OWNER);
  }

  @Test
  public void shouldConvertUserOwner() {
    Assertions.assertThat(toApplicationId.apply(USER_OWNER)).isEqualTo(USER_OWNER.getUserId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldConvertPublicOwnerFail() {
    toApplicationId.apply(PUBLIC_OWNER);
  }
}