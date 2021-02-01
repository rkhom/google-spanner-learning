CREATE TABLE dashboards (
  dashboard_id INT64 NOT NULL,
  title STRING(255),
  start_date TIMESTAMP,
  end_date TIMESTAMP,
) PRIMARY KEY (dashboard_id);

CREATE TABLE widgets (
  widget_id INT64 NOT NULL,
  dashboard_id INT64 NOT NULL,
  title STRING(255),
  type STRING(30),
) PRIMARY KEY (dashboard_id, widget_id),
  INTERLEAVE IN PARENT dashboards ON DELETE CASCADE;