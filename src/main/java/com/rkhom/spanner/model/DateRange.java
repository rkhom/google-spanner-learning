package com.rkhom.spanner.model;

import com.google.cloud.spring.data.spanner.core.mapping.Column;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DateRange {

  @Column(name = "start_date")
  private LocalDateTime startDate;

  @Column(name = "end_date")
  private LocalDateTime endDate;

}
