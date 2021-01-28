package com.rkhom.spanner.converter;

import com.google.cloud.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;

public class LocalDateTimeToTimestampConverter implements Converter<LocalDateTime, Timestamp> {

  @Override
  public Timestamp convert(LocalDateTime localDateTime) {
    return Timestamp.of(Date.from(localDateTime.toInstant(ZoneOffset.UTC)));
  }
}
