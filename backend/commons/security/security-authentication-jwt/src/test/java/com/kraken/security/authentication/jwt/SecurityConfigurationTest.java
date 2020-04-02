package com.kraken.security.authentication.jwt;

import com.google.common.collect.ImmutableList;
import com.kraken.Application;
import com.kraken.security.decoder.api.TokenDecoder;
import com.kraken.security.entity.KrakenUserTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityConfigurationTest {
  @Autowired
  WebTestClient webTestClient;

  @MockBean
  ReactiveJwtDecoder jwtDecoder;

  @MockBean
  TokenDecoder tokenDecoder;

  @Test
  public void shouldReturnUser() throws IOException {
    given(tokenDecoder.decode("token")).willReturn(KrakenUserTest.KRAKEN_USER);
    given(jwtDecoder.decode("token")).willReturn(Mono.just(JwtTestFactory.JWT_FACTORY.create(ImmutableList.of("USER"),
        ImmutableList.of("/default-kraken"), Optional.of("/default-kraken"))));

    webTestClient.get()
        .uri(uriBuilder -> uriBuilder.path("/test/user")
            .build())
        .header("Authorization", "Bearer token")
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .isEqualTo("hello username");
  }

  @Test
  public void shouldFailAdmin() {
    given(jwtDecoder.decode("token")).willReturn(Mono.just(JwtTestFactory.JWT_FACTORY.create(ImmutableList.of("USER"),
        ImmutableList.of("/default-kraken"), Optional.of("/default-kraken"))));

    webTestClient.get()
        .uri(uriBuilder -> uriBuilder.path("/test/admin")
            .build())
        .header("Authorization", "Bearer token")
        .exchange()
        .expectStatus().is4xxClientError();
  }
}
