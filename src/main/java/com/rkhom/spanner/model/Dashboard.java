package com.rkhom.spanner.model;

import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.Embedded;
import com.google.cloud.spring.data.spanner.core.mapping.Interleaved;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import com.rkhom.spanner.validator.ValidDateRange;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Table(name = "dashboards")
@Data
public class Dashboard {

  private static final int TITLE_MAX_LENGTH = 255;

  @PrimaryKey
  @Column(name = "dashboard_id")
  private Long id;

  @NotBlank
  @Size(max = TITLE_MAX_LENGTH)
  @ApiModelProperty(required = true)
  @Column(spannerTypeMaxLength = TITLE_MAX_LENGTH)
  private String title;

  @Embedded
  @NotNull
  @ValidDateRange
  @ApiModelProperty(required = true)
  private DateRange dateRange;

  @Valid
  @NotEmpty
  @Interleaved
  private List<Widget> widgets;

}
