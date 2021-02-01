package com.rkhom.spanner.model;

import com.google.cloud.spring.data.spanner.core.mapping.Column;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DateRange {

  @ApiModelProperty(required = true)
  @Column(name = "start_date")
  private LocalDateTime startDate;

  @ApiModelProperty(required = true)
  @Column(name = "end_date")
  private LocalDateTime endDate;

}
