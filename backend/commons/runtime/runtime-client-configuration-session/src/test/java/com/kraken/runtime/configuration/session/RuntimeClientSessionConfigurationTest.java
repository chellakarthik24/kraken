package com.kraken.runtime.configuration.session;

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
public class RuntimeClientSessionConfigurationTest {

  @Mock
  RuntimeClient runtimeClient;
  @Mock
  RuntimeClientFactory runtimeClientFactory;

  RuntimeClientSessionConfiguration configuration;

  @Before
  public void setUp() {
    given(runtimeClientFactory.create(AuthenticationMode.SESSION)).willReturn(runtimeClient);
    configuration = new RuntimeClientSessionConfiguration();
  }

  @Test
  public void shouldInjectClient() {
    Assertions.assertThat(configuration.runtimeClient(runtimeClientFactory)).isSameAs(runtimeClient);
  }

}