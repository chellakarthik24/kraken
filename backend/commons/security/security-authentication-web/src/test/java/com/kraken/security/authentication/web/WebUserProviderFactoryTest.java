package com.kraken.security.authentication.web;

import com.kraken.tests.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.kraken.security.authentication.api.AuthenticationMode.WEB;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class WebUserProviderFactoryTest {

  @Mock
  WebUserProvider provider;

  WebUserProviderFactory factory;

  @Before
  public void setUp() {
    factory = new WebUserProviderFactory(provider);
  }

  @Test
  public void shouldCreate() {
    final var userProvider = factory.create("userId");
    assertThat(userProvider).isSameAs(provider);
  }

  @Test
  public void shouldGetMode() {
    assertThat(factory.getMode()).isEqualTo(WEB);
  }

  @Test
  public void shouldPassNPE() {
    TestUtils.shouldPassNPE(WebUserProviderFactory.class);
  }

}