package com.kraken.storage.client.web;

import com.kraken.config.storage.api.StorageProperties;
import com.kraken.storage.client.api.StorageClient;
import com.kraken.storage.client.web.WebStorageClient;
import com.kraken.tools.configuration.jackson.JacksonConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JacksonConfiguration.class, WebStorageClient.class})
public class WebStoragePropertiesSpringTest {
  @Autowired
  StorageClient client;
  @MockBean
  StorageProperties properties;

  @Test
  public void shouldCreateWebClients() {
    assertThat(properties).isNotNull();
    assertThat(client).isNotNull();
  }
}
