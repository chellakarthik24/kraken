package com.kraken.security.authentication.client.spring;

import com.google.common.collect.ImmutableList;
import com.kraken.config.api.UrlProperty;
import com.kraken.security.authentication.api.ExchangeFilter;
import com.kraken.security.authentication.api.ExchangeFilterFactory;
import com.kraken.security.authentication.client.api.AuthenticatedClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.kraken.security.authentication.api.AuthenticationMode.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AbstractAuthenticatedClientFactoryTest {

  private static class TestAuthenticatedClient implements AuthenticatedClient {
    public final WebClient webClient;

    public TestAuthenticatedClient(final WebClient webClient) {
      this.webClient = webClient;
    }
  }

  private static class TestAuthenticatedClientFactory extends AbstractAuthenticatedClientFactory<TestAuthenticatedClient> {

    public TestAuthenticatedClientFactory(final List<ExchangeFilterFactory> exchangeFilterFactories,
                                          final UrlProperty property) {
      super(exchangeFilterFactories, property);
    }

    @Override
    protected TestAuthenticatedClient create(WebClient.Builder webClientBuilder) {
      return new TestAuthenticatedClient(webClientBuilder.build());
    }
  }

  @Mock
  UrlProperty property;
  @Mock
  ExchangeFilterFactory noopFilterFactory;
  @Mock
  ExchangeFilter noopExchangeFilter;
  @Mock
  ExchangeFilterFactory webFilterFactory;
  @Mock
  ExchangeFilter webExchangeFilter;

  TestAuthenticatedClientFactory factory;

  @Before
  public void setUp() {
    given(noopFilterFactory.getMode()).willReturn(NOOP);
    given(noopFilterFactory.create(Mockito.anyString())).willReturn(noopExchangeFilter);
    given(webFilterFactory.getMode()).willReturn(SESSION);
    given(webFilterFactory.create(Mockito.anyString())).willReturn(webExchangeFilter);
    given(property.getUrl()).willReturn("url");
    factory = new TestAuthenticatedClientFactory(ImmutableList.of(noopFilterFactory, webFilterFactory), property);
  }

  @Test
  public void shouldCreate() {
    factory.create();
    verify(noopFilterFactory).create("");
  }

  @Test
  public void shouldCreateWeb() {
    factory.create(SESSION);
    verify(webFilterFactory).create("");
  }

  @Test
  public void shouldCreateUserId() {
    factory.create(SESSION, "userId");
    verify(webFilterFactory).create("userId");
  }

  @Test(expected
      = IllegalArgumentException.class)
  public void shouldCreateImpersonateFail() {
    factory.create(IMPERSONATE);
  }
}