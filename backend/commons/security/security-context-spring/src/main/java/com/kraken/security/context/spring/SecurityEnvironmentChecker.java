package com.kraken.security.context.spring;

import com.kraken.runtime.context.api.environment.EnvironmentChecker;
import com.kraken.runtime.entity.task.TaskType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class SecurityEnvironmentChecker implements EnvironmentChecker {

  @Override
  public void accept(Map<String, String> stringStringMap) {

  }

  @Override
  public boolean test(TaskType taskType) {
    return false;
  }
}
