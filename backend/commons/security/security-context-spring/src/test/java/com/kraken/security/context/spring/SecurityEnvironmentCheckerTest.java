package com.kraken.security.context.spring;

import com.google.common.collect.ImmutableMap;
import com.kraken.runtime.entity.task.TaskType;
import com.kraken.test.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static com.kraken.tools.environment.KrakenEnvironmentKeys.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SecurityEnvironmentChecker.class)
public class SecurityEnvironmentCheckerTest {

  @Autowired
  SecurityEnvironmentChecker checker;

  @Test
  public void shouldTest() {
    Arrays.stream(TaskType.values()).forEach(taskType -> assertThat(checker.test(taskType)).isTrue());
  }


  @Test(expected = NullPointerException.class)
  public void shouldFailCheck() {
    checker.accept(ImmutableMap.of());
  }

  @Test
  public void shouldSucceed() {
    final var env = ImmutableMap.<String, String>builder()
        .put(KRAKEN_SECURITY_URL.name(), "value")
        .put(KRAKEN_SECURITY_APIID.name(), "value")
        .put(KRAKEN_SECURITY_APISECRET.name(), "value")
        .put(KRAKEN_SECURITY_WEBID.name(), "value")
        .put(KRAKEN_SECURITY_REALM.name(), "value")
        .put(KRAKEN_SECURITY_ACCESSTOKEN.name(), "value")
        .put(KRAKEN_SECURITY_REFRESHTOKEN.name(), "value")
        .build();
    checker.accept(env);
  }

  @Test
  public void shouldTestUtils() {
    TestUtils.shouldPassNPE(SecurityEnvironmentChecker.class);
  }
}