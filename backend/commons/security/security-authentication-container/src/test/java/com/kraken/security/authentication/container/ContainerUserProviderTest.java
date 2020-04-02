package com.kraken.security.authentication.container;

import com.google.common.collect.ImmutableList;
import com.kraken.config.security.container.api.SecurityContainerProperties;
import com.kraken.security.authentication.api.UserProvider;
import com.kraken.security.client.api.SecurityClient;
import com.kraken.security.decoder.api.TokenDecoder;
import com.kraken.security.entity.KrakenRole;
import com.kraken.security.entity.KrakenTokenTest;
import com.kraken.security.entity.KrakenUser;
import com.kraken.security.entity.KrakenUserTest;
import com.kraken.test.utils.TestUtils;
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
public class ContainerUserProviderTest {
  @Mock
  SecurityContainerProperties properties;

  @Mock
  TokenDecoder decoder;

  @Mock
  SecurityClient client;

  UserProvider userProvider;

  @Before
  public void setUp() {
    given(properties.getAccessToken()).willReturn("accessToken");
    given(properties.getRefreshToken()).willReturn("refreshToken");
    given(properties.getMinValidity()).willReturn(60L);
    userProvider = new ContainerUserProvider(properties, decoder, client);
  }

  @Test
  public void shouldGetAuthenticatedUser() throws IOException {
    given(decoder.decode("accessToken")).willReturn(KrakenUserTest.KRAKEN_USER);
    final var user = userProvider.getAuthenticatedUser().block();
    assertThat(user).isNotNull();
    assertThat(user).isEqualTo(KrakenUserTest.KRAKEN_USER);
  }

  @Test
  public void shouldGetTokenValue() throws IOException {
    given(client.refreshToken("refreshToken")).willReturn(Mono.just(KrakenTokenTest.KRAKEN_TOKEN));
    given(decoder.decode("accessToken")).willReturn(KrakenUserTest.KRAKEN_USER);
    final var token = userProvider.getTokenValue().block();
    assertThat(token).isNotNull();
    assertThat(token).isEqualTo("accessToken");
  }

  @Test
  public void shouldGetTokenValueNoRefresh() throws IOException {
    given(decoder.decode("accessToken")).willReturn( KrakenUser.builder()
        .issuedAt(Instant.now().plusSeconds(30))
        .expirationTime(Instant.now().plusSeconds(1800))
        .userId("userId")
        .username("username")
        .roles(ImmutableList.of(KrakenRole.USER))
        .groups(ImmutableList.of("/default-kraken"))
        .currentGroup("/default-kraken")
        .build());
    final var token = userProvider.getTokenValue().block();
    assertThat(token).isNotNull();
    assertThat(token).isEqualTo("accessToken");
  }

  @Test
  public void shouldPassNPE() {
    TestUtils.shouldPassNPE(ContainerUserProvider.class);
  }
}
