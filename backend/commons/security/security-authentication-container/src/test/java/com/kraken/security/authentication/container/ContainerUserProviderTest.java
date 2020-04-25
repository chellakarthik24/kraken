package com.kraken.security.authentication.container;

import com.kraken.config.security.client.api.SecurityClientCredentialsProperties;
import com.kraken.config.security.client.api.SecurityClientProperties;
import com.kraken.config.security.container.api.SecurityContainerProperties;
import com.kraken.security.client.api.SecurityClient;
import com.kraken.security.decoder.api.TokenDecoder;
import com.kraken.security.entity.user.KrakenRole;
import com.kraken.security.entity.user.KrakenTokenTest;
import com.kraken.security.entity.user.KrakenUser;
import com.kraken.security.entity.user.KrakenUserTest;
import com.kraken.tests.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

import static com.google.common.collect.ImmutableList.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static reactor.test.StepVerifier.create;

@RunWith(MockitoJUnitRunner.class)
public class ContainerUserProviderTest {
  @Mock
  SecurityClientProperties clientProperties;
  @Mock
  SecurityClientCredentialsProperties credentialsProperties;
  @Mock
  SecurityContainerProperties containerProperties;
  @Mock
  TokenDecoder decoder;
  @Mock
  SecurityClient client;
  @Mock
  Function<Long, Mono<String>> refresh;

  ContainerUserProvider userProvider;

  @Before
  public void setUp() throws IOException {
    given(containerProperties.getAccessToken()).willReturn("accessToken");
    given(containerProperties.getRefreshToken()).willReturn("refreshToken");
    given(clientProperties.getContainer()).willReturn(credentialsProperties);
    given(decoder.decode(any())).willReturn(KrakenUserTest.KRAKEN_USER);
    userProvider = new ContainerUserProvider(clientProperties, containerProperties, decoder, client);
  }

  @Test
  public void shouldCreateToken() {
    given(client.refreshToken(credentialsProperties, "refreshToken")).willReturn(Mono.just(KrakenTokenTest.KRAKEN_TOKEN));
    final var token = userProvider.newToken().block();
    assertThat(token).isNotNull();
    assertThat(token).isEqualTo(KrakenTokenTest.KRAKEN_TOKEN);
  }

  @Test
  public void shouldRefreshToken() {
    given(client.refreshToken(credentialsProperties, "refreshToken")).willReturn(Mono.just(KrakenTokenTest.KRAKEN_TOKEN));
    final var tokenRefresh = userProvider.refreshToken(KrakenTokenTest.KRAKEN_TOKEN).block();
    assertThat(tokenRefresh).isNotNull();
    assertThat(tokenRefresh).isEqualTo(KrakenTokenTest.KRAKEN_TOKEN);
  }

  @Test
  public void shouldPeriodicRefresh() throws IOException {
    final var now = Instant.now();
    given(refresh.apply(any())).willAnswer(invocation -> Mono.just(invocation.getArgument(0).toString()));
    given(decoder.decode(any())).willReturn(KrakenUser.builder()
        .issuedAt(now)
        .expirationTime(now.plusSeconds(300))
        .userId("userId")
        .username("username")
        .roles(of(KrakenRole.USER))
        .groups(of("/default-kraken"))
        .currentGroup("/default-kraken")
        .build());

    StepVerifier
        .withVirtualTime(() -> userProvider.periodicRefresh(refresh), 3)
        .expectSubscription()
        .expectNext("0")
        .thenAwait(Duration.ofSeconds(600))
        .expectNext("1", "2")
        .expectComplete()
        .verify(Duration.ofSeconds(600));

    verify(refresh).apply(0L);
    verify(refresh).apply(1L);
    verify(refresh).apply(2L);
  }

  @Test
  public void shouldPassNPE() {
    TestUtils.shouldPassNPE(ContainerUserProvider.class);
  }
}