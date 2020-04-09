package com.kraken.storage.client.web;

import com.kraken.tests.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

public class WebStorageClientFactoryTest {

  @Test
  public void shouldTestNPE() {
    TestUtils.shouldPassNPE(WebStorageClientFactory.class);
  }

}