package com.kraken.runtime.entity.log;

import org.junit.Test;

import static com.kraken.tests.utils.TestUtils.shouldPassAll;

public class LogTest {

  public static final Log LOG = Log.builder()
      .applicationId("applicationId")
      .id("id")
      .type(LogType.CONTAINER)
      .text("text")
      .status(LogStatus.RUNNING)
      .build();


  @Test
  public void shouldPassTestUtils() {
    shouldPassAll(LOG);
  }

}
