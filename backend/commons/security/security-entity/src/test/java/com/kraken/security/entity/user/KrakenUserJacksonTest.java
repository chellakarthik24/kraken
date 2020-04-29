package com.kraken.security.entity.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kraken.security.entity.owner.Owner;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ObjectMapper.class)
public class KrakenUserJacksonTest {

  @Autowired
  private ObjectMapper mapper;

  @Test
  public void shouldSerializeUser() throws IOException {
    final var object = KrakenUserTest.KRAKEN_USER;
    final String json = mapper.writeValueAsString(object);
    Assertions.assertThat(mapper.readValue(json, KrakenUser.class)).isEqualTo(object);
  }

}
