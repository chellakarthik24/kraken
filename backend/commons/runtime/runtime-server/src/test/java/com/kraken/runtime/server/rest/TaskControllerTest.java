package com.kraken.runtime.server.rest;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.kraken.runtime.backend.api.TaskService;
import com.kraken.runtime.context.api.ExecutionContextService;
import com.kraken.runtime.context.entity.CancelContextTest;
import com.kraken.runtime.context.entity.ExecutionContextTest;
import com.kraken.runtime.entity.environment.ExecutionEnvironmentTest;
import com.kraken.runtime.entity.task.Task;
import com.kraken.runtime.entity.task.TaskTest;
import com.kraken.runtime.event.*;
import com.kraken.runtime.server.service.TaskListService;
import com.kraken.tests.security.AuthControllerTest;
import com.kraken.tests.utils.TestUtils;
import com.kraken.tools.event.bus.EventBus;
import com.kraken.tools.sse.SSEService;
import com.kraken.tools.sse.SSEWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static com.kraken.tools.environment.KrakenEnvironmentKeys.KRAKEN_DESCRIPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class TaskControllerTest extends RuntimeControllerTest {

  @Test
  public void shouldPassTestUtils() {
    TestUtils.shouldPassNPE(TaskController.class);
  }

  @Test
  public void shouldRun() {
    final var env = ExecutionEnvironmentTest.EXECUTION_ENVIRONMENT;
    final var context = ExecutionContextTest.EXECUTION_CONTEXT;
    final var applicationId = context.getApplicationId();
    final var taskId = context.getTaskId();

    given(contextService.newExecuteContext(applicationId, env)).willReturn(Mono.just(context));
    given(taskService.execute(context))
        .willReturn(Mono.just(context));

    webTestClient.post()
        .uri(uriBuilder -> uriBuilder.path("/task")
            .build())
        .header("Authorization", "Bearer user-token")
        .header("ApplicationId", applicationId)
        .body(BodyInserters.fromValue(env))
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .isEqualTo(taskId);

    verify(eventBus).publish(TaskExecutedEvent.builder()
        .context(context)
        .build());
  }

  @Test
  public void shouldFailToRun() {
    final var applicationId = "applicationId"; // Should match [a-z0-9]*
    final var env = ImmutableMap.of(KRAKEN_DESCRIPTION.name(), "description");
    webTestClient.post()
        .uri(uriBuilder -> uriBuilder.path("/task")
            .build())
        .header("Authorization", "Bearer user-token")
        .header("ApplicationId", applicationId)
        .body(BodyInserters.fromValue(env))
        .exchange()
        .expectStatus().is5xxServerError();
  }

  @Test
  public void shouldCancel() {
    final var context = CancelContextTest.CANCEL_CONTEXT;
    final var applicationId = context.getApplicationId();
    final var taskId = context.getTaskId();
    final var taskType = context.getTaskType();

    given(contextService.newCancelContext(applicationId, taskId, taskType)).willReturn(Mono.just(context));

    given(taskService.cancel(context))
        .willReturn(Mono.just(context));

    webTestClient.delete()
        .uri(uriBuilder -> uriBuilder.path("/task/cancel")
            .pathSegment(taskType.toString())
            .queryParam("taskId", taskId)
            .build())
        .header("Authorization", "Bearer user-token")
        .header("ApplicationId", applicationId)
        .exchange()
        .expectStatus().isOk();

    verify(taskService).cancel(context);
    verify(eventBus).publish(TaskCancelledEvent.builder().context(context)
        .build());
  }

  @Test
  public void shouldRemove() {
    final var context = CancelContextTest.CANCEL_CONTEXT;
    final var applicationId = context.getApplicationId();
    final var taskId = context.getTaskId();
    final var taskType = context.getTaskType();

    given(contextService.newCancelContext(applicationId, taskId, taskType)).willReturn(Mono.just(context));

    given(taskService.remove(context))
        .willReturn(Mono.just(context));

    webTestClient.delete()
        .uri(uriBuilder -> uriBuilder.path("/task/remove")
            .pathSegment(taskType.toString())
            .queryParam("taskId", taskId)
            .build())
        .header("Authorization", "Bearer user-token")
        .header("ApplicationId", applicationId)
        .exchange()
        .expectStatus().isOk();

    verify(taskService).remove(context);
  }

  @Test
  public void shouldFailToCancel() {
    final var applicationId = "applicationId"; // Should match [a-z0-9]*
    webTestClient.delete()
        .uri(uriBuilder -> uriBuilder.path("/task/cancel")
            .pathSegment("GATLING_RUN")
            .queryParam("taskId", "taskId")
            .build())
        .header("Authorization", "Bearer user-token")
        .header("ApplicationId", applicationId)
        .exchange()
        .expectStatus().is5xxServerError();
  }

  @Test
  public void shouldList() {
    final var tasksFlux = Flux.just(TaskTest.TASK);
    given(taskListService.list(Optional.of("app")))
        .willReturn(tasksFlux);

    webTestClient.get()
        .uri("/task/list")
        .header("Authorization", "Bearer user-token")
        .header("ApplicationId", "app")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Task.class)
        .contains(TaskTest.TASK);
  }

  @Test
  public void shouldWatch() {
    final List<Task> list = ImmutableList.of(TaskTest.TASK, TaskTest.TASK);
    final Flux<List<Task>> tasksFlux = Flux.just(list, list);
    final Flux<ServerSentEvent<List<Task>>> eventsFlux = Flux.just(ServerSentEvent.builder(list).build(), ServerSentEvent.builder(list).build());
    given(taskListService.watch(Optional.of("app"))).willReturn(tasksFlux);
    given(sse.keepAlive(tasksFlux)).willReturn(eventsFlux);

    final var result = webTestClient.get()
        .uri("/task/watch")
        .header("ApplicationId", "app")
        .header("Authorization", "Bearer user-token")
        .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
        .expectBody()
        .returnResult();
    final var body = new String(Optional.ofNullable(result.getResponseBody()).orElse(new byte[0]), Charsets.UTF_8);
    assertThat(body).isEqualTo("data:[{\"id\":\"id\",\"startDate\":42,\"status\":\"STARTING\",\"type\":\"GATLING_RUN\",\"containers\":[],\"expectedCount\":2,\"description\":\"description\",\"applicationId\":\"app\"},{\"id\":\"id\",\"startDate\":42,\"status\":\"STARTING\",\"type\":\"GATLING_RUN\",\"containers\":[],\"expectedCount\":2,\"description\":\"description\",\"applicationId\":\"app\"}]\n" +
        "\n" +
        "data:[{\"id\":\"id\",\"startDate\":42,\"status\":\"STARTING\",\"type\":\"GATLING_RUN\",\"containers\":[],\"expectedCount\":2,\"description\":\"description\",\"applicationId\":\"app\"},{\"id\":\"id\",\"startDate\":42,\"status\":\"STARTING\",\"type\":\"GATLING_RUN\",\"containers\":[],\"expectedCount\":2,\"description\":\"description\",\"applicationId\":\"app\"}]\n" +
        "\n");
  }

  @Test
  public void shouldEventsFail() {
    webTestClient.get()
        .uri("/task/events")
        .header("Authorization", "Bearer user-token")
        .exchange()
        .expectStatus().is4xxClientError();
  }

  @Test
  public void shouldEvents() {
    given(eventBus.of(TaskExecutedEvent.class)).willReturn(Flux.just(TaskExecutedEventTest.TASK_EXECUTED_EVENT));
    given(eventBus.of(TaskCreatedEvent.class)).willReturn(Flux.just(TaskCreatedEventTest.TASK_CREATED_EVENT));
    given(eventBus.of(TaskStatusUpdatedEvent.class)).willReturn(Flux.just(TaskStatusUpdatedEventTest.TASK_STATUS_UPDATED_EVENT));
    given(eventBus.of(TaskCancelledEvent.class)).willReturn(Flux.just(TaskCancelledEventTest.TASK_CANCELLED_EVENT));
    given(eventBus.of(TaskRemovedEvent.class)).willReturn(Flux.just(TaskRemovedEventTest.TASK_REMOVED_EVENT));
    final var wrapped = Flux.just(SSEWrapper.builder().type("TaskExecutedEvent").value(TaskExecutedEventTest.TASK_EXECUTED_EVENT).build());
    given(sse.merge(any())).willReturn(wrapped);
    final Flux<ServerSentEvent<SSEWrapper>> eventsFlux = wrapped.map(sseWrapper -> ServerSentEvent.<SSEWrapper>builder().data(sseWrapper).build());
    given(sse.keepAlive(wrapped)).willReturn(eventsFlux);

    final var result = webTestClient.get()
        .uri("/task/events")
        .header("Authorization", "Bearer api-token")
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
        .expectBody()
        .returnResult();

    final var body = new String(Optional.ofNullable(result.getResponseBody()).orElse(new byte[0]), Charsets.UTF_8);
    assertThat(body).isEqualTo("data:{\"type\":\"TaskExecutedEvent\",\"value\":{\"context\":{\"applicationId\":\"application\",\"taskId\":\"taskId\",\"taskType\":\"GATLING_RUN\",\"description\":\"description\",\"templates\":{\"hostId\":\"template\"}}}}\n" +
        "\n");
  }
}