package com.rkhom.spanner.service;

import com.rkhom.spanner.model.Dashboard;
import com.rkhom.spanner.model.Widget;
import com.rkhom.spanner.repository.DashboardRepository;
import com.rkhom.spanner.repository.WidgetRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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

  private final DashboardRepository dashboardRepository;

  private final WidgetRepository widgetRepository;

  private final IdGenerator idGenerator;

  public Dashboard findDashboardById(Long dashboardId) {
    return dashboardRepository.findById(dashboardId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            String.format(DASHBOARD_NOT_FOUND_ERROR, dashboardId)));
  }

  public List<Dashboard> findAllDashboards() {
    return dashboardRepository.findAll();
  }

  public Long createDashboard(Dashboard dashboard) {
    dashboard.setId(idGenerator.generateDashboardId());
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
          widget.setId(idGenerator.generateWidgetId());
          widget.setDashboardId(dashboard.getId());
        });
  }

}
