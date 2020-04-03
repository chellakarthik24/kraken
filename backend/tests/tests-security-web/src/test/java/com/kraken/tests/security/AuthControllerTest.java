package com.kraken.tests.security;

import com.google.common.collect.ImmutableList;
import com.kraken.Application;
import com.kraken.security.decoder.api.TokenDecoder;
import com.kraken.security.entity.KrakenUserTest;
import org.junit.Before;
import org.junit.runner.RunWith;
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
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AuthControllerTest {

  @Autowired
  protected WebTestClient webTestClient;
  @MockBean
  protected ReactiveJwtDecoder jwtDecoder;
  @MockBean
  protected TokenDecoder tokenDecoder;

  @Before
  public void setUp() throws IOException {
    given(jwtDecoder.decode("user-token")).willReturn(Mono.just(JwtTestFactory.JWT_FACTORY.create("user-token", ImmutableList.of("USER"),
        ImmutableList.of("/default-kraken"), Optional.of("/default-kraken"))));
    given(tokenDecoder.decode("user-token")).willReturn(KrakenUserTest.KRAKEN_USER);
    given(jwtDecoder.decode("admin-token")).willReturn(Mono.just(JwtTestFactory.JWT_FACTORY.create("admin-token", ImmutableList.of("ADMIN"),
        ImmutableList.of("/default-kraken"), Optional.of("/default-kraken"))));
    given(tokenDecoder.decode("admin-token")).willReturn(KrakenUserTest.KRAKEN_ADMIN);
  }


}
