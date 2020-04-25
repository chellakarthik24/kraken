package com.kraken.security.authentication.container;

import com.kraken.config.security.client.api.SecurityClientProperties;
import com.kraken.config.security.container.api.SecurityContainerProperties;
import com.kraken.security.client.api.SecurityClient;
import com.kraken.security.decoder.api.TokenDecoder;
import com.kraken.security.entity.user.KrakenUserTest;
import com.kraken.tests.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static com.kraken.security.authentication.api.AuthenticationMode.CONTAINER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ContainerUserProviderFactoryTest {

  @Mock
  SecurityClientProperties clientProperties;
  @Mock
  SecurityContainerProperties containerProperties;
  @Mock
  TokenDecoder decoder;
  @Mock
  SecurityClient client;

  ContainerUserProviderFactory factory;

  @Before
  public void setUp() throws IOException{
    given(containerProperties.getAccessToken()).willReturn("accessToken");
    given(containerProperties.getRefreshToken()).willReturn("refreshToken");
    given(decoder.decode(any())).willReturn(KrakenUserTest.KRAKEN_USER);
    factory = new ContainerUserProviderFactory(clientProperties, containerProperties, decoder, client);
  }

  @Test
  public void shouldCreate() {
    final var userProvider = factory.create("userId");
    assertThat(userProvider).isNotNull();
  }

  @Test
  public void shouldGetMode() {
    assertThat(factory.getMode()).isEqualTo(CONTAINER);
  }

  @Test
  public void shouldPassNPE() {
    TestUtils.shouldPassNPE(ContainerUserProviderFactory.class);
  }

}