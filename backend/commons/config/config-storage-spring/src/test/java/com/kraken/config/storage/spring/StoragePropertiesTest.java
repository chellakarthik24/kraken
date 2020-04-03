package com.kraken.config.storage.spring;

import com.kraken.tests.utils.TestUtils;
import org.junit.Test;

public class StoragePropertiesTest {

  public static final SpringStorageProperties STORAGE_PROPERTIES = SpringStorageProperties.builder()
      .url("storageUrl")
      .build();

  @Test
  public void shouldPassTestUtils() {
    TestUtils.shouldPassAll(STORAGE_PROPERTIES);
  }

}
