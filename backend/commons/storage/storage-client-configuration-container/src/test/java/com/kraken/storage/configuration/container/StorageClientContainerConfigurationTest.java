package com.kraken.storage.configuration.container;

import com.kraken.security.authentication.api.AuthenticationMode;
import com.kraken.storage.client.api.StorageClient;
import com.kraken.storage.client.api.StorageClientFactory;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
@RunWith(MockitoJUnitRunner.class)
public class StorageClientContainerConfigurationTest {

  @Mock
  StorageClient storageClient;
  @Mock
  StorageClientFactory storageClientFactory;

  StorageClientContainerConfiguration configuration;

  @Before
  public void setUp() {
    given(storageClientFactory.create(AuthenticationMode.CONTAINER)).willReturn(storageClient);
    configuration = new StorageClientContainerConfiguration();
  }

  @Test
  public void shouldInjectClient() {
    Assertions.assertThat(configuration.storageClient(storageClientFactory)).isSameAs(storageClient);
  }

}