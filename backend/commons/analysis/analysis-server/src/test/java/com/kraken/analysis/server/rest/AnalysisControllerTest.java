package com.kraken.analysis.server.rest;

import com.kraken.analysis.entity.DebugEntryTest;
import com.kraken.analysis.entity.ResultStatus;
import com.kraken.analysis.entity.ResultTest;
import com.kraken.analysis.server.service.AnalysisService;
import com.kraken.storage.client.api.StorageClient;
import com.kraken.storage.entity.StorageNode;
import com.kraken.storage.entity.StorageNodeTest;
import com.kraken.tests.security.AuthControllerTest;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static com.kraken.storage.entity.StorageNodeType.FILE;
import static com.kraken.tests.utils.TestUtils.shouldPassNPE;
import static org.mockito.BDDMockito.given;

public class AnalysisControllerTest extends AuthControllerTest {
  @MockBean
  AnalysisService service;
  @MockBean
  StorageClient storageClient;

  @Test
  public void shouldPassTestUtils() {
    shouldPassNPE(AnalysisController.class);
  }

  @Test
  public void shouldCreate() {
    final var result = ResultTest.RESULT;
    final var node = StorageNodeTest.STORAGE_NODE;
    given(service.create(result))
        .willReturn(Mono.just(node));

    webTestClient.post()
        .uri(uriBuilder -> uriBuilder.path("/result")
            .build())
        .header("Authorization", "Bearer user-token")
        .body(BodyInserters.fromValue(result))
        .exchange()
        .expectStatus().isOk()
        .expectBody(StorageNode.class)
        .isEqualTo(node);
  }

  @Test
  public void shouldCreateForbidden() {
    final var result = ResultTest.RESULT;
    webTestClient.post()
        .uri(uriBuilder -> uriBuilder.path("/result")
            .build())
        .header("Authorization", "Bearer no-role-token")
        .body(BodyInserters.fromValue(result))
        .exchange()
        .expectStatus().is4xxClientError();
  }

  @Test
  public void shouldDelete() {
    final var resultId = "resultId";
    given(service.delete(resultId))
        .willReturn(Mono.just(resultId));

    webTestClient.delete()
        .uri(uriBuilder -> uriBuilder.path("/result")
            .queryParam("resultId", resultId)
            .build())
        .header("Authorization", "Bearer user-token")
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .isEqualTo(resultId);
  }

  @Test
  public void shouldDeleteForbidden() {
    final var resultId = "resultId";
    webTestClient.delete()
        .uri(uriBuilder -> uriBuilder.path("/result")
            .queryParam("resultId", resultId)
            .build())
        .header("Authorization", "Bearer no-role-token")
        .exchange()
        .expectStatus().is4xxClientError();
  }

  @Test
  public void shouldSetStatus() {
    final var resultId = "resultId";
    final var status = ResultStatus.COMPLETED;
    final var resultNode = StorageNode.builder()
        .depth(1)
        .path("path/result.son")
        .type(FILE)
        .length(0L)
        .lastModified(0L)
        .build();

    given(service.setStatus(resultId, status))
        .willReturn(Mono.just(resultNode));

    webTestClient.post()
        .uri(uriBuilder -> uriBuilder.path("/result/status/COMPLETED")
            .queryParam("resultId", resultId)
            .build())
        .header("Authorization", "Bearer user-token")
        .exchange()
        .expectStatus().isOk()
        .expectBody(StorageNode.class)
        .isEqualTo(resultNode);
  }

  @Test
  public void shouldSetStatusForbidden() {
    final var resultId = "resultId";
    webTestClient.post()
        .uri(uriBuilder -> uriBuilder.path("/result/status/COMPLETED")
            .queryParam("resultId", resultId)
            .build())
        .header("Authorization", "Bearer no-role-token")
        .exchange()
        .expectStatus().is4xxClientError();
  }

  @Test
  public void shouldAddDebug() {
    final var debug = DebugEntryTest.DEBUG_ENTRY;

    given(service.addDebug(debug))
        .willReturn(Mono.fromCallable(() -> null));

    webTestClient.post()
        .uri(uriBuilder -> uriBuilder.path("/result/debug")
            .build())
        .header("Authorization", "Bearer user-token")
        .body(BodyInserters.fromValue(debug))
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  public void shouldAddDebugForbidden() {
    final var debug = DebugEntryTest.DEBUG_ENTRY;
    webTestClient.post()
        .uri(uriBuilder -> uriBuilder.path("/result/debug")
            .build())
        .header("Authorization", "Bearer no-role-token")
        .body(BodyInserters.fromValue(debug))
        .exchange()
        .expectStatus().is4xxClientError();
  }
}

