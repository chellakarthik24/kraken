package com.kraken.security.context.spring;

import com.kraken.runtime.context.api.environment.EnvironmentChecker;
import com.kraken.runtime.entity.task.TaskType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.kraken.tools.environment.KrakenEnvironmentKeys.*;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class SecurityEnvironmentChecker implements EnvironmentChecker {

  @Override
  public void accept(Map<String, String> environment) {
    requireEnv(environment,
        KRAKEN_SECURITY_URL,
        KRAKEN_SECURITY_APIID,
        KRAKEN_SECURITY_APISECRET,
        KRAKEN_SECURITY_WEBID,
        KRAKEN_SECURITY_REALM,
        KRAKEN_SECURITY_ACCESSTOKEN,
        KRAKEN_SECURITY_REFRESHTOKEN);
  }

  @Override
  public boolean test(TaskType taskType) {
    return true;
  }
}