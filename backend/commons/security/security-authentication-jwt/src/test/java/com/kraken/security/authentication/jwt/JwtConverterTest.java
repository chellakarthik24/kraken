package com.kraken.security.authentication.jwt;

import com.google.common.collect.ImmutableList;
import com.kraken.config.security.jwt.api.JwtClaimsProperties;
import com.kraken.config.security.jwt.api.JwtProperties;
import com.kraken.security.entity.KrakenRole;
import com.kraken.security.entity.KrakenUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableList.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class JwtConverterTest {

  @Mock
  JwtProperties jwtProperties;

  @Mock
  JwtClaimsProperties claimsProperties;

  JwtConverter converter;

  @Before
  public void setUp() {
    converter = new JwtConverter(jwtProperties);
    given(jwtProperties.getClaims()).willReturn(claimsProperties);
    given(claimsProperties.getCurrentGroup()).willReturn("current_group");
    given(claimsProperties.getUsername()).willReturn("preferred_username");
    given(claimsProperties.getGroups()).willReturn("user_groups");
  }

  @Test
  public void shouldConvertNoGroup() {
    final var jwt = JwtTestFactory.JWT_FACTORY.create(ImmutableList.of("USER"),
        ImmutableList.of("/default-kraken"), Optional.empty());
    final var result = converter.convert(jwt);
    assertThat(result).isNotNull();
    final var token = result.block();
    assertThat(token).isNotNull();
    assertThat(token.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toUnmodifiableList())).isEqualTo(ImmutableList.of("USER"));
    assertThat(token.getDetails()).isEqualTo(KrakenUser.builder()
        .userId("userId")
        .currentGroup("")
        .username("username")
        .groups(of("/default-kraken"))
        .roles(of(KrakenRole.USER))
        .build());
  }

  @Test
  public void shouldConvertWithGroup() {
    final var jwt = JwtTestFactory.JWT_FACTORY.create(ImmutableList.of("USER"),
        ImmutableList.of("/default-kraken"), Optional.of("/default-kraken"));

    final var result = converter.convert(jwt);
    assertThat(result).isNotNull();
    final var token = result.block();
    assertThat(token).isNotNull();
    assertThat(token.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toUnmodifiableList())).isEqualTo(ImmutableList.of("USER"));
    assertThat(token.getDetails()).isEqualTo(KrakenUser.builder()
        .userId("userId")
        .currentGroup("/default-kraken")
        .username("username")
        .groups(of("/default-kraken"))
        .roles(of(KrakenRole.USER))
        .build());
  }

  @Test(expected = AuthenticationServiceException.class)
  public void shouldNotConvertGroupMismatch() {
    final var jwt = JwtTestFactory.JWT_FACTORY.create(ImmutableList.of("USER"),
        ImmutableList.of("/default-kraken"), Optional.of("other group"));

    final var result = converter.convert(jwt);
    assertThat(result).isNotNull();
    result.block();
  }

}