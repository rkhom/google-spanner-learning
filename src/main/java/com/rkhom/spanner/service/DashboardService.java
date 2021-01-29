package com.rkhom.spanner.service;

import com.google.cloud.spanner.Statement;
import com.google.cloud.spring.data.spanner.core.SpannerPageableQueryOptions;
import com.google.cloud.spring.data.spanner.core.SpannerQueryOptions;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.rkhom.spanner.model.Dashboard;
import com.rkhom.spanner.model.Widget;
import com.rkhom.spanner.repository.DashboardRepository;
import com.rkhom.spanner.repository.WidgetRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service for performing CRUD operations with dashboards.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

  private static final String DASHBOARD_NOT_FOUND_ERROR = "Dashboard with ID %d is not found.";
  private static final String INVALID_WIDGETS_ERROR = "Widgets with IDs %s have invalid dashboard ID or widget ID.";
  private static final String IO_ERROR = "Cannot read file %s.";

  private static final String FIND_BY_ID_QUERY_FILE = "find_dashboard_by_id.sql";

  private static final String DASHBOARD_ID = "dashboardId";

  private final DashboardRepository dashboardRepository;

  private final WidgetRepository widgetRepository;

  private final SpannerTemplate spannerTemplate;

  public Dashboard findDashboardById(Long dashboardId) {
    String query;
    try {
      query = Files.readString(new ClassPathResource(FIND_BY_ID_QUERY_FILE).getFile().toPath());
    } catch (IOException e) {
      throw new IllegalStateException(String.format(IO_ERROR, FIND_BY_ID_QUERY_FILE), e);
    }

    Statement findByIdStatement = Statement.newBuilder(query)
        .bind(DASHBOARD_ID)
        .to(dashboardId)
        .build();

    return spannerTemplate.query(Dashboard.class, findByIdStatement, new SpannerQueryOptions())
        .stream()
        .findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            String.format(DASHBOARD_NOT_FOUND_ERROR, dashboardId)));
  }

  public List<Dashboard> findAllDashboards() {
    return spannerTemplate.queryAll(Dashboard.class, new SpannerPageableQueryOptions());
  }

  public Long createDashboard(Dashboard dashboard) {
    dashboard.setId(Instant.now().toEpochMilli());
    fillNewWidgetIds(dashboard);

    return dashboardRepository.save(dashboard).getId();
  }

  public void deleteDashboard(Long dashboardId) {
    dashboardRepository.delete(findDashboardById(dashboardId));
  }

  @Transactional
  public Long updateDashboard(Dashboard updatedDashboard) {
    Dashboard existedDashboard = findDashboardById(updatedDashboard.getId());

    validateUpdatedDashboard(updatedDashboard, existedDashboard);
    deleteWidgets(updatedDashboard, existedDashboard);
    fillNewWidgetIds(updatedDashboard);

    return dashboardRepository.save(updatedDashboard).getId();
  }

  private void validateUpdatedDashboard(Dashboard updatedDashboard, Dashboard existedDashboard) {
    List<Long> existedWidgetsIds = existedDashboard.getWidgets().stream()
        .map(Widget::getId)
        .collect(Collectors.toList());

    List<Long> invalidWidgetsIds = updatedDashboard.getWidgets().stream()
        .filter(widget -> widget.getId() != null)
        .filter(widget -> widget.getDashboardId() != null)
        .filter(widget -> !(existedWidgetsIds.contains(widget.getId())
            && widget.getDashboardId().equals(existedDashboard.getId())))
        .map(Widget::getId)
        .collect(Collectors.toList());

    if (!invalidWidgetsIds.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(INVALID_WIDGETS_ERROR, invalidWidgetsIds));
    }
  }

  private void deleteWidgets(Dashboard updatedDashboard, Dashboard existedDashboard) {
    List<Widget> widgetsToRemove = existedDashboard.getWidgets().stream()
        .filter(widget -> !updatedDashboard.getWidgets().contains(widget))
        .collect(Collectors.toList());

    widgetRepository.deleteAll(widgetsToRemove);
  }

  private void fillNewWidgetIds(Dashboard dashboard) {
    dashboard.getWidgets().stream()
        .filter(widget -> widget.getId() == null)
        .forEach(widget -> {
          widget.setId(ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE));
          widget.setDashboardId(dashboard.getId());
        });
  }

}
