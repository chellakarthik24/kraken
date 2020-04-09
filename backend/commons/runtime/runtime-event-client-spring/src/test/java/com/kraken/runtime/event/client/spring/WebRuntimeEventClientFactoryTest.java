package com.kraken.runtime.event.client.spring;

import com.kraken.tests.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

public class WebRuntimeEventClientFactoryTest {

  @Test
  public void shouldTestNPE() {
    TestUtils.shouldPassNPE(WebRuntimeEventClientFactory.class);
  }

}