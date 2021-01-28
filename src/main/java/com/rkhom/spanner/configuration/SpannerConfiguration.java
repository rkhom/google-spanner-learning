package com.rkhom.spanner.configuration;

import com.google.cloud.spring.data.spanner.core.convert.ConverterAwareMappingSpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.convert.SpannerEntityProcessor;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.rkhom.spanner.converter.LocalDateTimeToTimestampConverter;
import com.rkhom.spanner.converter.TimestampToLocalDateTimeConverter;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpannerConfiguration {

  @Bean
  public SpannerEntityProcessor spannerEntityProcessor(SpannerMappingContext spannerMappingContext) {
    return new ConverterAwareMappingSpannerEntityProcessor(spannerMappingContext,
        List.of(new LocalDateTimeToTimestampConverter()),
        List.of(new TimestampToLocalDateTimeConverter()));
  }

  @Bean
  public SpannerMappingContext spannerMappingContext() {
    return new SpannerMappingContext();
  }

}
