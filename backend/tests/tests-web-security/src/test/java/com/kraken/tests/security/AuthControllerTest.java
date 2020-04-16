package com.kraken.tests.security;

import com.google.common.collect.ImmutableList;
import com.kraken.Application;
import com.kraken.security.decoder.api.TokenDecoder;
import com.kraken.security.entity.owner.UserOwner;
import com.kraken.security.entity.user.KrakenUserTest;
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

import static com.kraken.security.entity.user.KrakenRole.ADMIN;
import static com.kraken.security.entity.user.KrakenRole.USER;
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

  protected UserOwner userOwner = UserOwner.builder().applicationId("app").userId(KrakenUserTest.KRAKEN_USER.getUserId()).build();
  protected UserOwner adminOwner = UserOwner.builder().applicationId("app").userId(KrakenUserTest.KRAKEN_ADMIN.getUserId()).build();

  @Before
  public void setUp() throws IOException {
    // User
    given(jwtDecoder.decode("user-token")).willReturn(Mono.just(JwtTestFactory.JWT_FACTORY.create("user-token", ImmutableList.of(USER.name()),
        ImmutableList.of("/default-kraken"), Optional.of("/default-kraken"))));
    given(tokenDecoder.decode("user-token")).willReturn(KrakenUserTest.KRAKEN_USER);
    // Admin
    given(jwtDecoder.decode("admin-token")).willReturn(Mono.just(JwtTestFactory.JWT_FACTORY.create("admin-token", ImmutableList.of(ADMIN.name()),
        ImmutableList.of("/default-kraken"), Optional.of("/default-kraken"))));
    given(tokenDecoder.decode("admin-token")).willReturn(KrakenUserTest.KRAKEN_ADMIN);
    // Admin
    given(jwtDecoder.decode("api-token")).willReturn(Mono.just(JwtTestFactory.JWT_FACTORY.create("api-token", ImmutableList.of("API"),
        ImmutableList.of(), Optional.empty())));
    given(tokenDecoder.decode("api-token")).willReturn(KrakenUserTest.KRAKEN_API);
    // No role
    given(jwtDecoder.decode("no-role-token")).willReturn(Mono.just(JwtTestFactory.JWT_FACTORY.create("no-role-token", ImmutableList.of(),
        ImmutableList.of(), Optional.empty())));
    given(tokenDecoder.decode("no-role-token")).willReturn(KrakenUserTest.KRAKEN_ADMIN);
  }


}
