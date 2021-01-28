package com.rkhom.spanner.converter;

import com.google.cloud.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.core.convert.converter.Converter;

public class TimestampToLocalDateTimeConverter implements Converter<Timestamp, LocalDateTime> {

  @Override
  public LocalDateTime convert(Timestamp timestamp) {
    return LocalDateTime
        .ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos(), ZoneOffset.UTC);
  }
}
