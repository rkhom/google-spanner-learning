package com.rkhom.spanner.model;

import com.google.cloud.spring.data.spanner.core.mapping.Column;
import com.google.cloud.spring.data.spanner.core.mapping.PrimaryKey;
import com.google.cloud.spring.data.spanner.core.mapping.Table;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Table(name = "widgets")
@Data
public class Widget {

  private static final int TITLE_MAX_LENGTH = 255;
  private static final int TYPE_MAX_LENGTH = 30;

  @PrimaryKey(keyOrder = 2)
  @Column(name = "widget_id")
  private Long id;

  @PrimaryKey
  @Column(name = "dashboard_id")
  private Long dashboardId;

  @NotBlank
  @Size(max = TITLE_MAX_LENGTH)
  @ApiModelProperty(required = true)
  @Column(spannerTypeMaxLength = TITLE_MAX_LENGTH)
  private String title;

  @NotNull
  @ApiModelProperty(required = true)
  @Column(spannerTypeMaxLength = TYPE_MAX_LENGTH)
  private WidgetType type;

}
