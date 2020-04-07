package com.kraken.runtime.server.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
public class RuntimeSecurityConfiguration {
  @Bean
  SecurityWebFilterChain springSecurityFilterChain(final ServerHttpSecurity http, final Converter<Jwt, Mono<AbstractAuthenticationToken>> converter) {
    http
        .authorizeExchange()
        .pathMatchers(HttpMethod.OPTIONS).permitAll()
        .pathMatchers("/container/**").hasAnyAuthority("USER", "ADMIN")
        .pathMatchers("/host/list").hasAnyAuthority("USER", "ADMIN")
        .pathMatchers("/host/**").hasAnyAuthority("ADMIN")
        .pathMatchers("/logs/**").hasAnyAuthority("USER", "ADMIN")
        .pathMatchers("/task/**").hasAnyAuthority("USER", "ADMIN")
        .anyExchange().denyAll()
        .and()
        .oauth2ResourceServer()
        .jwt()
        .jwtAuthenticationConverter(converter);
    return http.build();
  }

}
