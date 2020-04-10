package com.kraken.runtime.context.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kraken.runtime.entity.task.Container;
import com.kraken.runtime.entity.task.ContainerStatus;
import com.kraken.runtime.entity.task.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Value
@Builder
public class ExecutionContext {
  String applicationId;
  String taskId;
  TaskType taskType;
  String description;
  //  Key: hostId, Value; template specific to this host
  Map<String, String> templates;

  @JsonCreator
  ExecutionContext(
      @NonNull @JsonProperty("applicationId") final String applicationId,
      @NonNull @JsonProperty("taskId") final String taskId,
      @NonNull @JsonProperty("taskType") final TaskType taskType,
      @NonNull @JsonProperty("description") final String description,
      @NonNull @JsonProperty("templates") final Map<String, String> templates
  ) {
    super();
    this.applicationId = applicationId;
    this.taskId = taskId;
    this.taskType = taskType;
    this.description = description;
    this.templates = templates;
  }
}
