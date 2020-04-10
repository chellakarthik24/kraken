package com.kraken.runtime.configuration.container;

import com.kraken.runtime.client.api.RuntimeClient;
import com.kraken.runtime.client.api.RuntimeClientFactory;
import com.kraken.security.authentication.api.AuthenticationMode;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
@RunWith(MockitoJUnitRunner.class)
public class RuntimeClientContainerConfigurationTest {

  @Mock
  RuntimeClient runtimeClient;
  @Mock
  RuntimeClientFactory runtimeClientFactory;

  RuntimeClientContainerConfiguration configuration;

  @Before
  public void setUp() {
    given(runtimeClientFactory.create(AuthenticationMode.CONTAINER)).willReturn(runtimeClient);
    configuration = new RuntimeClientContainerConfiguration();
  }

  @Test
  public void shouldInjectClient() {
    Assertions.assertThat(configuration.runtimeClient(runtimeClientFactory)).isSameAs(runtimeClient);
  }

}