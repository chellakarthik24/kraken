package com.kraken.runtime.web.configuration;

import com.kraken.runtime.client.api.RuntimeClient;
import com.kraken.runtime.client.api.RuntimeClientFactory;
import com.kraken.runtime.container.configuration.RuntimeClientWebConfiguration;
import com.kraken.security.authentication.api.AuthenticationMode;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
@RunWith(MockitoJUnitRunner.class)
public class RuntimeClientWebConfigurationTest {

  @Mock
  RuntimeClient runtimeClient;
  @Mock
  RuntimeClientFactory runtimeClientFactory;

  RuntimeClientWebConfiguration configuration;

  @Before
  public void setUp() {
    given(runtimeClientFactory.create(AuthenticationMode.WEB)).willReturn(runtimeClient);
    configuration = new RuntimeClientWebConfiguration();
  }

  @Test
  public void shouldInjectClient() {
    Assertions.assertThat(configuration.runtimeClient(runtimeClientFactory)).isSameAs(runtimeClient);
  }

}