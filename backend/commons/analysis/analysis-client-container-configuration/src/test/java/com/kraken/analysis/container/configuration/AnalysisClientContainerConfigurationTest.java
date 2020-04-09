package com.kraken.analysis.container.configuration;

import com.kraken.analysis.client.api.AnalysisClient;
import com.kraken.analysis.client.api.AnalysisClientFactory;
import com.kraken.security.authentication.api.AuthenticationMode;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AnalysisClientContainerConfigurationTest {

  @Mock
  AnalysisClient analysisClient;
  @Mock
  AnalysisClientFactory analysisClientFactory;

  AnalysisClientContainerConfiguration configuration;

  @Before
  public void setUp() {
    given(analysisClientFactory.create(AuthenticationMode.CONTAINER)).willReturn(analysisClient);
    configuration = new AnalysisClientContainerConfiguration();
  }

  @Test
  public void shouldInjectClient() {
    Assertions.assertThat(configuration.analysisClient(analysisClientFactory)).isSameAs(analysisClient);
  }

}