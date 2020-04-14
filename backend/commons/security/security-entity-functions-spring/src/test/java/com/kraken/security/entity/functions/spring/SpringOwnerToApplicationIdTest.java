package com.kraken.security.entity.functions.spring;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static com.kraken.security.entity.owner.ApplicationOwnerTest.APPLICATION_OWNER;
import static com.kraken.security.entity.owner.PublicOwnerTest.PUBLIC_OWNER;
import static com.kraken.security.entity.owner.UserOwnerTest.USER_OWNER;

public class SpringOwnerToApplicationIdTest {

  SpringOwnerToApplicationId toApplicationId;

  @Before
  public void setUp() {
    toApplicationId = new SpringOwnerToApplicationId();
  }

  @Test
  public void shouldConvertApplicationOwner() {
    Assertions.assertThat(toApplicationId.apply(APPLICATION_OWNER)).isEqualTo(APPLICATION_OWNER.getApplicationId());
  }

  @Test
  public void shouldConvertUserOwner() {
    Assertions.assertThat(toApplicationId.apply(USER_OWNER)).isEqualTo(USER_OWNER.getApplicationId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldConvertPublicOwnerFail() {
    toApplicationId.apply(PUBLIC_OWNER);
  }
}