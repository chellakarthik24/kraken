package com.kraken.security.client.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.kraken.config.security.client.api.SecurityClientCredentialsProperties;
import com.kraken.config.security.client.api.SecurityClientProperties;
import com.kraken.security.entity.token.KrakenToken;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class KeycloakSecurityClientTest {

  private ObjectMapper mapper;
  private MockWebServer server;
  private KeycloakSecurityClient client;

  @Mock
  SecurityClientProperties properties;
  @Mock
  SecurityClientCredentialsProperties apiCredentials;
  @Mock
  SecurityClientCredentialsProperties webCredentials;
  @Mock
  SecurityClientCredentialsProperties containerCredentials;

  @Before
  public void setUp() {
    server = new MockWebServer();
    mapper = new ObjectMapper();
    final String url = server.url("/").toString();
    given(properties.getUrl()).willReturn(url);
    given(properties.getRealm()).willReturn("kraken");
    given(webCredentials.getId()).willReturn("kraken-web");
    given(apiCredentials.getId()).willReturn("kraken-api");
    given(apiCredentials.getSecret()).willReturn("api-secret");
    given(containerCredentials.getId()).willReturn("kraken-container");
    given(containerCredentials.getSecret()).willReturn("container-secret");
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

    final var token = client.userLogin(webCredentials, "username", "password").block();
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
  public void shouldClientLogin() throws IOException, InterruptedException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(mapper.writeValueAsString(ImmutableMap.of("access_token", "accessToken", "refresh_token", "refreshToken")))
    );

    final var token = client.clientLogin(apiCredentials).block();
    assertThat(token).isNotNull();
    assertThat(token).isEqualTo(KrakenToken.builder()
        .accessToken("accessToken")
        .refreshToken("refreshToken")
        .build());

    final var request = server.takeRequest();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getPath()).isEqualTo("/auth/realms/kraken/protocol/openid-connect/token");
    assertThat(request.getBody().readUtf8()).isEqualTo("client_id=kraken-api&client_secret=api-secret&grant_type=client_credentials");
  }

  @Test
  public void shouldExchangeToken() throws InterruptedException, IOException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(mapper.writeValueAsString(ImmutableMap.of("access_token", "accessToken", "refresh_token", "refreshToken")))
    );

    final var token = client.exchangeToken(containerCredentials, "accessToken").block();
    assertThat(token).isNotNull();
    assertThat(token).isEqualTo(KrakenToken.builder()
        .accessToken("accessToken")
        .refreshToken("refreshToken")
        .build());

    final var request = server.takeRequest();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getPath()).isEqualTo("/auth/realms/kraken/protocol/openid-connect/token");
    assertThat(request.getBody().readUtf8()).isEqualTo("client_id=kraken-container&client_secret=container-secret&grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Atoken-exchange&subject_token=accessToken&requested_token_type=urn%3Aietf%3Aparams%3Aoauth%3Atoken-type%3Arefresh_token&audience=kraken-container");
  }


  @Test
  public void shouldRefreshToken() throws InterruptedException, IOException {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(mapper.writeValueAsString(ImmutableMap.of("access_token", "accessToken", "refresh_token", "refreshToken")))
    );

    final var token = client.refreshToken(containerCredentials, "refreshToken").block();
    assertThat(token).isNotNull();
    assertThat(token).isEqualTo(KrakenToken.builder()
        .accessToken("accessToken")
        .refreshToken("refreshToken")
        .build());

    final var request = server.takeRequest();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getPath()).isEqualTo("/auth/realms/kraken/protocol/openid-connect/token");
    assertThat(request.getBody().readUtf8()).isEqualTo("grant_type=refresh_token&refresh_token=refreshToken&client_id=kraken-container&client_secret=container-secret");
  }
}