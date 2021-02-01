package com.rkhom.spanner.repository;

import com.google.cloud.spanner.Statement;
import com.google.cloud.spring.data.spanner.core.SpannerPageableQueryOptions;
import com.google.cloud.spring.data.spanner.core.SpannerQueryOptions;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.rkhom.spanner.model.Dashboard;
import com.rkhom.spanner.utils.FileUtils;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository("dashboardRepositoryImpl")
@RequiredArgsConstructor
public class DashboardTemplateRepositoryImpl implements TemplateRepository<Dashboard, Long> {

  private static final String FIND_BY_ID_QUERY_FILE = "find_dashboard_by_id.sql";

  private static final String DASHBOARD_ID = "dashboardId";

  private final SpannerTemplate spannerTemplate;

  @Override
  public Optional<Dashboard> findById(Long id) {
    Statement findByIdStatement = Statement
        .newBuilder(FileUtils.readFileAsString(FIND_BY_ID_QUERY_FILE))
        .bind(DASHBOARD_ID)
        .to(id)
        .build();

    return spannerTemplate.query(Dashboard.class, findByIdStatement, new SpannerQueryOptions())
        .stream()
        .findFirst();
  }

  @Override
  public List<Dashboard> findAll() {
    return spannerTemplate.queryAll(Dashboard.class, new SpannerPageableQueryOptions());
  }
}
