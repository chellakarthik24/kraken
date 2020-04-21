package com.kraken.analysis.client.web;

import com.kraken.analysis.client.api.AnalysisClient;
import com.kraken.analysis.client.api.AnalysisClientBuilder;
import com.kraken.analysis.entity.ResultTest;
import com.kraken.security.authentication.api.AuthenticationMode;
import com.kraken.tests.utils.TestConfiguration;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore("start keycloak and make serve-storage/runtime-docker/analysis before running")
@RunWith(SpringRunner.class)
@ContextConfiguration(
    classes = {TestConfiguration.class},
    initializers = {ConfigFileApplicationContextInitializer.class})
public class WebAnalysisClientIntegrationTest {

  @Autowired
  private AnalysisClientBuilder analysisClientBuilder;

  AnalysisClient analysisClient;

  @Before
  public void setUp(){
    analysisClient = analysisClientBuilder.applicationId("gatling").mode(AuthenticationMode.IMPERSONATE, "kraken-user").build();
  }

  @Test
  public void shouldCreateResult() {
    analysisClient.create(ResultTest.RESULT).block();
  }
}
