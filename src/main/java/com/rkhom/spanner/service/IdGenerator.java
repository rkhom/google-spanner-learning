package com.rkhom.spanner.service;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

@Component
public class IdGenerator {

  public Long generateDashboardId() {
    return Instant.now().toEpochMilli();
  }

  public Long generateWidgetId() {
    return ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE);
  }


}
