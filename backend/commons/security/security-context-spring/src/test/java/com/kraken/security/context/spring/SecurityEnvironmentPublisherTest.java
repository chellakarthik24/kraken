package com.kraken.security.context.spring;

import com.kraken.Application;
import com.kraken.config.security.client.api.SecurityClientProperties;
import com.kraken.runtime.context.entity.ExecutionContextBuilderTest;
import com.kraken.runtime.entity.environment.ExecutionEnvironmentEntry;
import com.kraken.runtime.entity.task.TaskType;
import com.kraken.security.authentication.api.UserProvider;
import com.kraken.security.client.api.SecurityClient;
import com.kraken.security.entity.KrakenTokenTest;
import com.kraken.test.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static com.google.common.collect.ImmutableList.of;
import static com.kraken.runtime.entity.environment.ExecutionEnvironmentEntrySource.SECURITY;
import static com.kraken.tools.environment.KrakenEnvironmentKeys.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SecurityEnvironmentPublisher.class)
public class SecurityEnvironmentPublisherTest {

  @Autowired
  SecurityEnvironmentPublisher publisher;
  @MockBean
  SecurityClientProperties clientProperties;
  @MockBean
  UserProvider userProvider;
  @MockBean
  SecurityClient client;

  @Test
  public void shouldTest() {
    Arrays.stream(TaskType.values()).forEach(taskType -> assertThat(publisher.test(taskType)).isTrue());
  }

  @Test
  public void shouldGet() {
    given(clientProperties.getUrl()).willReturn("url");
    given(clientProperties.getApiId()).willReturn("apiId");
    given(clientProperties.getApiSecret()).willReturn("apiSecret");
    given(clientProperties.getWebId()).willReturn("webId");
    given(clientProperties.getRealm()).willReturn("realm");
    given(userProvider.getTokenValue()).willReturn(Mono.just("token"));
    given(client.exchangeToken("token")).willReturn(Mono.just(KrakenTokenTest.KRAKEN_TOKEN));
    final var env = publisher.apply(ExecutionContextBuilderTest.EXECUTION_CONTEXT_BUILDER).block();
    assertThat(env).isNotNull();
    assertThat(env).isEqualTo(of(
        ExecutionEnvironmentEntry.builder().from(SECURITY).scope("").key(KRAKEN_SECURITY_URL.name()).value("url").build(),
        ExecutionEnvironmentEntry.builder().from(SECURITY).scope("").key(KRAKEN_SECURITY_APIID.name()).value("apiId").build(),
        ExecutionEnvironmentEntry.builder().from(SECURITY).scope("").key(KRAKEN_SECURITY_APISECRET.name()).value("apiSecret").build(),
        ExecutionEnvironmentEntry.builder().from(SECURITY).scope("").key(KRAKEN_SECURITY_WEBID.name()).value("webId").build(),
        ExecutionEnvironmentEntry.builder().from(SECURITY).scope("").key(KRAKEN_SECURITY_REALM.name()).value("realm").build(),
        ExecutionEnvironmentEntry.builder().from(SECURITY).scope("").key(KRAKEN_SECURITY_ACCESSTOKEN.name()).value("accessToken").build(),
        ExecutionEnvironmentEntry.builder().from(SECURITY).scope("").key(KRAKEN_SECURITY_REFRESHTOKEN.name()).value("refreshToken").build()
    ));
  }

  @Test
  public void shouldTestUtils(){
    TestUtils.shouldPassNPE(SecurityEnvironmentPublisher.class);
  }
}

