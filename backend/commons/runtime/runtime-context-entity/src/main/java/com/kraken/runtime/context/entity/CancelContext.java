package com.kraken.runtime.context.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kraken.runtime.entity.task.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class CancelContext {
  String applicationId;
  String taskId;
  TaskType taskType;

  @JsonCreator
  CancelContext(
      @NonNull @JsonProperty("applicationId") final String applicationId,
      @NonNull @JsonProperty("taskId") final String taskId,
      @NonNull @JsonProperty("taskType") final TaskType taskType
  ) {
    super();
    this.applicationId = applicationId;
    this.taskId = taskId;
    this.taskType = taskType;
  }
}
