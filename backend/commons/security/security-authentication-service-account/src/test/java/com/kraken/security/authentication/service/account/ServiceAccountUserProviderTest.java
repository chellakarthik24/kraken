package com.kraken.security.authentication.service.account;

import com.google.common.collect.ImmutableList;
import com.kraken.config.security.client.api.SecurityClientCredentialsProperties;
import com.kraken.config.security.client.api.SecurityClientProperties;
import com.kraken.security.authentication.api.UserProvider;
import com.kraken.security.authentication.service.account.ServiceAccountUserProvider;
import com.kraken.security.client.api.SecurityClient;
import com.kraken.security.decoder.api.TokenDecoder;
import com.kraken.security.entity.KrakenRole;
import com.kraken.security.entity.KrakenTokenTest;
import com.kraken.security.entity.KrakenUser;
import com.kraken.security.entity.KrakenUserTest;
import com.kraken.tests.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ServiceAccountUserProviderTest {
  @Mock
  SecurityClientProperties clientProperties;
  @Mock
  SecurityClientCredentialsProperties credentialsProperties;
  @Mock
  TokenDecoder decoder;
  @Mock
  SecurityClient client;

  UserProvider userProvider;

  @Before
  public void setUp() {
    given(clientProperties.getApi()).willReturn(credentialsProperties);
    userProvider = new ServiceAccountUserProvider(clientProperties, decoder, client);
  }

  @Test
  public void shouldGetTokenValue() throws IOException {
    // First login
    given(decoder.decode("accessToken")).willReturn(KrakenUserTest.KRAKEN_USER);
    given(client.clientLogin(credentialsProperties)).willReturn(Mono.just(KrakenTokenTest.KRAKEN_TOKEN));
    final var token = userProvider.getTokenValue().block();
    assertThat(token).isNotNull();
    assertThat(token).isEqualTo("accessToken");

    // Then refresh
    given(client.refreshToken(credentialsProperties, "refreshToken")).willReturn(Mono.just(KrakenTokenTest.KRAKEN_TOKEN));
    final var tokenRefresh = userProvider.getTokenValue().block();
    assertThat(tokenRefresh).isNotNull();
    assertThat(tokenRefresh).isEqualTo("accessToken");

    // Then same
    final var decoded = KrakenUser.builder()
        .issuedAt(Instant.now().plusSeconds(30))
        .expirationTime(Instant.now().plusSeconds(1800))
        .userId("userId")
        .username("username")
        .roles(ImmutableList.of(KrakenRole.USER))
        .groups(ImmutableList.of("/default-kraken"))
        .currentGroup("/default-kraken")
        .build();
    given(decoder.decode("accessToken")).willReturn(decoded);
    final var tokenSame = userProvider.getTokenValue().block();
    assertThat(tokenSame).isNotNull();
    assertThat(tokenSame).isSameAs(tokenRefresh);

    // Finally user
    final var user = userProvider.getAuthenticatedUser().block();
    assertThat(user).isNotNull();
    assertThat(user).isEqualTo(decoded);
  }

  @Test
  public void shouldPassNPE() {
    TestUtils.shouldPassNPE(ServiceAccountUserProvider.class);
  }
}