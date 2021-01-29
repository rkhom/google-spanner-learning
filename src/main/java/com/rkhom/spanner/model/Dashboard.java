package com.rkhom.spanner.model;

import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.Embedded;
import com.google.cloud.spring.data.spanner.core.mapping.Interleaved;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import java.util.List;
import lombok.Data;

@Table(name = "dashboards")
@Data
public class Dashboard {

  @PrimaryKey
  @Column(name = "dashboard_id")
  private Long id;

  @Column(spannerTypeMaxLength = 255)
  private String title;

  @Embedded
  private DateRange dateRange;

  @Interleaved
  private List<Widget> widgets;

}
