package com.kraken.security.decoder.spring;

import com.google.common.collect.ImmutableList;
import com.kraken.Application;
import com.kraken.security.decoder.api.TokenDecoder;
import com.kraken.security.entity.KrakenRole;
import com.kraken.security.entity.KrakenUser;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.Instant;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SpringTokenDecoderTest {

  @Autowired
  TokenDecoder decoder;

  @Test
  public void shouldDecodeAccessToken() throws IOException {
    final var user = decoder.decode("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJZb19LT2IzeUZncGlzM05tT1F2OFE0N2ZJQlltbkpsZEtlRE1LQ1lBQThjIn0.eyJleHAiOjE1ODU4MzI1MTYsImlhdCI6MTU4NTgzMjIxNiwianRpIjoiNGZiNjkyNjktOTc2Ny00ZDcyLWJiMjQtYzY2NzBjYjcxNzM2IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MDgwL2F1dGgvcmVhbG1zL2tyYWtlbiIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIyZTQ0ZmZhZS0xMTFjLTRmNTktYWUyYi02NTAwMGRlNmY3YjciLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJrcmFrZW4tYXBpIiwic2Vzc2lvbl9zdGF0ZSI6IjAwOWEzYTZiLTgyMDMtNDEwMS1hYmYxLWU1NzU5NTNkZjFiZCIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsIlVTRVIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJjdXJyZW50X2dyb3VwIjoiL2RlZmF1bHQta3Jha2VuIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInByZWZlcnJlZF91c2VybmFtZSI6ImtyYWtlbi11c2VyIiwidXNlcl9ncm91cHMiOlsiL2RlZmF1bHQta3Jha2VuIl19.K3YF2yZo2vqtGI7JeXPcoZzHhef6h_PMfGVhirmB4AgHjmtiAKdMXh545b8mO2QYaR8QKV2tT1N56_2kSHf1Ankk8DBBxExJawrsoZ9AV2zHwkBtuowNewgpJ2DL1FJNmNty86aihLIJT0j1VG8K8nRhujBjIO9BLCI5vvmlmPMXI15LTWwRycPAaxeRaTD33GIQDuy2jRA96xr-fPmlxrwb_WFH6XEREuMlXT9K6Cu8WKE9mAvmxLhlwZScLxKe2g8FG4Zy58ZHZ03Vk-Pn_zPDBMCE9jkAhthGIYHDk4Iml7qIgmBqKfDHW8hzv829y_4SkmY6K-Zj1-SALrtdkQ");
    Assertions.assertThat(user)
        .isEqualTo(
            KrakenUser.builder()
                .username("kraken-user")
                .userId("2e44ffae-111c-4f59-ae2b-65000de6f7b7")
                .roles(ImmutableList.of(KrakenRole.USER))
                .groups(ImmutableList.of("/default-kraken"))
                .currentGroup("/default-kraken")
                .sessionId("009a3a6b-8203-4101-abf1-e575953df1bd")
                .issuedAt(Instant.ofEpochMilli(1585832216))
                .expirationTime(Instant.ofEpochMilli(1585832516))
                .build()
        );
  }

  @Test
  public void shouldDecodeRefreshToken() throws IOException {
    final var user = decoder.decode("eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4MGY1ZTAyMS04M2MxLTQzNzUtOWE4YS1kNTFlNzI4ZDQ5MWQifQ.eyJleHAiOjE1ODU4MzQwMTYsImlhdCI6MTU4NTgzMjIxNiwianRpIjoiZDg5ZDI1YzItMzQ4Zi00ZmQxLWEyN2UtMzk3ZTcxNmU4MzAxIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MDgwL2F1dGgvcmVhbG1zL2tyYWtlbiIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6OTA4MC9hdXRoL3JlYWxtcy9rcmFrZW4iLCJzdWIiOiIyZTQ0ZmZhZS0xMTFjLTRmNTktYWUyYi02NTAwMGRlNmY3YjciLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoia3Jha2VuLWFwaSIsInNlc3Npb25fc3RhdGUiOiIwMDlhM2E2Yi04MjAzLTQxMDEtYWJmMS1lNTc1OTUzZGYxYmQiLCJzY29wZSI6ImVtYWlsIHByb2ZpbGUifQ.YnNECMEvcTL_N7dj9bFjWcGNCEB63H1YD0FFcHvw2jI");
    Assertions.assertThat(user)
        .isEqualTo(
            KrakenUser.builder()
                .username("")
                .userId("2e44ffae-111c-4f59-ae2b-65000de6f7b7")
                .roles(ImmutableList.of())
                .groups(ImmutableList.of())
                .currentGroup("")
                .sessionId("009a3a6b-8203-4101-abf1-e575953df1bd")
                .issuedAt(Instant.ofEpochMilli(1585832216))
                .expirationTime(Instant.ofEpochMilli(1585834016))
                .build()
        );
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailToDecodeMalformattedToken() throws IOException {
    decoder.decode("abc");
  }

  @Test(expected = IOException.class)
  public void shouldFailToDecodeMalformattedJSon() throws IOException {
    decoder.decode("asd.bqsdqs.cqsdqsd");
  }


  @Test(expected = IllegalArgumentException.class)
  public void shouldFailToDecodeGroupMismatch() throws IOException {
    decoder.decode("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJZb19LT2IzeUZncGlzM05tT1F2OFE0N2ZJQlltbkpsZEtlRE1LQ1lBQThjIn0.eyJleHAiOjE1ODU4Mzc0ODYsImlhdCI6MTU4NTgzNzE4NiwianRpIjoiN2VkMDg3NzYtNjE5NC00YjlkLTk5MzktYzljZGI4MTc3ZWQyIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MDgwL2F1dGgvcmVhbG1zL2tyYWtlbiIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIyZTQ0ZmZhZS0xMTFjLTRmNTktYWUyYi02NTAwMGRlNmY3YjciLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJrcmFrZW4td2ViIiwic2Vzc2lvbl9zdGF0ZSI6IjY3MDQ3MTNiLTQ1YTYtNGRmYS05ZDBiLTI0MTZjNzMyMTU3ZCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsIlVTRVIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJjdXJyZW50X2dyb3VwIjoiL290aGVyLWdyb3VwIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInByZWZlcnJlZF91c2VybmFtZSI6ImtyYWtlbi11c2VyIiwidXNlcl9ncm91cHMiOlsiL2RlZmF1bHQta3Jha2VuIl19.EL-VlLgpu7uzil7Ucy6R02cGXcrxkv-mq53jSDqgRYvZEwECrYf-IYzN2vJlSyxeu6c5QWogmtngaisMnMBmSKa4mnHCw0dtp5ie_OosEsWN7HDkAMNMJLfCfIq7sgGZvpFl2muSc6TNbwhM4OSQSOC_jtQdQNhHggQ1tMOXpJ5rWGCYizMyblnDrjUB0udYsX1eUZYUnMms_FX8YIrxw9Yq72y0YTXPdwEvaDX_u28gcSrnMbgHdETsEIQkmtVWQ2rN-BEQeVk50bpiWnf5OKS2T7XoK9R2Mz1z__ycSh5BxKiNY6yyz-9sdlRALNEQbKRMGhvcq5reIIJwZ1rWxg\n");
  }
}