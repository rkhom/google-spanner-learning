package com.rkhom.spanner.model;

import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import lombok.Data;

@Table(name = "widgets")
@Data
public class Widget {

  @PrimaryKey(keyOrder = 2)
  @Column(name = "widget_id")
  private Long id;

  @PrimaryKey
  @Column(name = "dashboard_id")
  private Long dashboardId;

  private String title;

  private WidgetType type;

}
