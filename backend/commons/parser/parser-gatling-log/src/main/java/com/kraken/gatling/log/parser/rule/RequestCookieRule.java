package com.kraken.gatling.log.parser.rule;

import com.kraken.analysis.entity.DebugEntry;
import com.kraken.gatling.log.parser.context.ParserContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.kraken.gatling.log.parser.context.LogParserState.HTTP_REQUEST;
import static com.kraken.gatling.log.parser.context.LogParserSubState.COOKIES;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
final class RequestCookieRule implements ContentParserRule {

  @NonNull
  ParserContext parserContext;

  @Override
  public Optional<DebugEntry> apply(final String line) {
    parserContext.appendRequestCookie(line);
    return Optional.empty();
  }

  @Override
  public boolean test(final String line) {
    return parserContext.getState() == HTTP_REQUEST && parserContext.getSubState() == COOKIES;
  }

}
