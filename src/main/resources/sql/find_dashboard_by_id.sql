SELECT dashboard_id, title, start_date, end_date,
  ARRAY (SELECT AS STRUCT widget_id, dashboard_id, title, type FROM widgets
  WHERE widgets.dashboard_id = dashboards.dashboard_id) AS widgets
  FROM dashboards
  WHERE dashboard_id = @dashboardId