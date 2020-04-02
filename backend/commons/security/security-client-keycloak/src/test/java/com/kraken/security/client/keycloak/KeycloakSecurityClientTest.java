package com.kraken.security.client.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.kraken.config.security.client.api.SecurityClientProperties;
import com.kraken.security.entity.KrakenToken;
import com.kraken.security.entity.KrakenTokenTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KeycloakSecurityClientTest {

  private ObjectMapper mapper;
  private MockWebServer server;
  private KeycloakSecurityClient client;

  @Mock
  SecurityClientProperties properties;

  @Before
  public void setUp() {
    server = new MockWebServer();
    mapper = new ObjectMapper();
    final String url = server.url("/").toString();
    given(properties.getUrl()).willReturn(url);
    given(properties.getApiId()).willReturn("kraken-api");
    given(properties.getApiSecret()).willReturn("secret");
    given(properties.getWebId()).willReturn("kraken-web");
    given(properties.getRealm()).willReturn("kraken");
    client = new KeycloakSecurityClient(properties);
  }

  @After
  public void tearDown() throws IOException {
    server.shutdown();
  }

  @Test
  public void shouldUserLogin() throws IOException, InterruptedException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(mapper.writeValueAsString(ImmutableMap.of("access_token", "accessToken", "refresh_token", "refreshToken")))
    );

    final var token = client.userLogin("username", "password").block();
    assertThat(token).isNotNull();
    assertThat(token).isEqualTo(KrakenToken.builder()
        .accessToken("accessToken")
        .refreshToken("refreshToken")
        .build());

    final var request = server.takeRequest();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getPath()).isEqualTo("/auth/realms/kraken/protocol/openid-connect/token");
    assertThat(request.getBody().readUtf8()).isEqualTo("username=username&password=password&grant_type=password&client_id=kraken-web");
  }

  @Test
  public void shouldExchangeToken() throws InterruptedException, IOException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(mapper.writeValueAsString(ImmutableMap.of("access_token", "accessToken", "refresh_token", "refreshToken")))
    );

    final var token = client.exchangeToken("accessToken").block();
    assertThat(token).isNotNull();
    assertThat(token).isEqualTo(KrakenToken.builder()
        .accessToken("accessToken")
        .refreshToken("refreshToken")
        .build());

    final var request = server.takeRequest();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getPath()).isEqualTo("/auth/realms/kraken/protocol/openid-connect/token");
    assertThat(request.getBody().readUtf8()).isEqualTo("client_id=kraken-api&client_secret=secret&grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Atoken-exchange&subject_token=accessToken&requested_token_type=urn%3Aietf%3Aparams%3Aoauth%3Atoken-type%3Arefresh_token&audience=kraken-api");
  }


  @Test
  public void shouldRefreshToken() throws InterruptedException, IOException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(mapper.writeValueAsString(ImmutableMap.of("access_token", "accessToken", "refresh_token", "refreshToken")))
    );

    final var token = client.refreshToken("refreshToken").block();
    assertThat(token).isNotNull();
    assertThat(token).isEqualTo(KrakenToken.builder()
        .accessToken("accessToken")
        .refreshToken("refreshToken")
        .build());

    final var request = server.takeRequest();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getPath()).isEqualTo("/auth/realms/kraken/protocol/openid-connect/token");
    assertThat(request.getBody().readUtf8()).isEqualTo("grant_type=refresh_token&refresh_token=refreshToken&client_id=kraken-api&client_secret=secret");
  }
}