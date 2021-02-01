package com.rkhom.spanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.rkhom.spanner.model.Dashboard;
import com.rkhom.spanner.model.DateRange;
import com.rkhom.spanner.model.Widget;
import com.rkhom.spanner.model.WidgetType;
import com.rkhom.spanner.repository.DashboardRepository;
import com.rkhom.spanner.repository.WidgetRepository;
import com.rkhom.spanner.service.DashboardService;
import com.rkhom.spanner.service.IdGenerator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

public class DashboardServiceTest {

  private static final Long DASHBOARD_ID = 1L;

  private static final Long WIDGET1_ID = 100L;

  private static final Long WIDGET2_ID = 101L;

  private static final Long WIDGET3_ID = 102L;

  @Mock
  private DashboardRepository dashboardRepository;

  @Mock
  private WidgetRepository widgetRepository;

  @Mock
  private IdGenerator idGenerator;

  @InjectMocks
  private DashboardService dashboardService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void findDashboardById_shouldReturnDashboard_whenDashboardIsFound() {
    Dashboard dashboard = createDashboard(2);

    given(dashboardRepository.findById(DASHBOARD_ID)).willReturn(Optional.of(dashboard));

    assertEquals(dashboard, dashboardService.findDashboardById(DASHBOARD_ID));
  }

  @Test
  void findDashboardById_shouldThrowResponseStatusException_whenDashboardIsNotFound() {
    given(dashboardRepository.findById(DASHBOARD_ID)).willThrow(ResponseStatusException.class);

    assertThrows(ResponseStatusException.class,
        () -> dashboardService.findDashboardById(DASHBOARD_ID));
  }

  @Test
  void findAllDashboards_shouldReturnAllDashboards() {
    List<Dashboard> dashboards = List.of(createDashboard(2));

    given(dashboardRepository.findAll()).willReturn(dashboards);

    assertEquals(dashboards, dashboardService.findAllDashboards());
  }

  @Test
  void createDashboard_shouldCreateNewDashboard() {
    Dashboard dashboardFromRequest = createDashboard(2);
    dashboardFromRequest.getWidgets().forEach(widget -> {
      widget.setId(null);
      widget.setDashboardId(null);
    });

    Dashboard savedDashboard = createDashboard(2);

    given(idGenerator.generateDashboardId()).willReturn(DASHBOARD_ID);
    given(idGenerator.generateWidgetId()).willReturn(WIDGET1_ID, WIDGET2_ID);
    given(dashboardRepository.save(dashboardFromRequest)).willReturn(savedDashboard);

    Long actual = dashboardService.createDashboard(dashboardFromRequest);

    then(idGenerator).should().generateDashboardId();
    then(idGenerator).should(times(2)).generateWidgetId();

    assertEquals(savedDashboard.getId(), actual);
  }

  @Test
  @SuppressWarnings("unchecked")
  void updateDashboard_shouldUpdateExistingDashboard_whenWidgetIsRemovedAndWidgetIdsAreCorrect() {
    Dashboard dashboardFromRequest = createDashboard(1);
    Dashboard existedDashboard = createDashboard(2);

    given(dashboardRepository.findById(DASHBOARD_ID)).willReturn(Optional.of(existedDashboard));
    given(dashboardRepository.save(dashboardFromRequest)).willReturn(dashboardFromRequest);

    Long actual = dashboardService.updateDashboard(dashboardFromRequest);

    then(widgetRepository).should()
        .deleteAll(argThat(widgets -> ((List<Widget>) widgets).size() == 1));

    assertEquals(dashboardFromRequest.getId(), actual);
  }

  @Test
  @SuppressWarnings("unchecked")
  void updateDashboard_shouldUpdateExistingDashboard_whenWidgetIsAddedAndWidgetIdsAreCorrect() {
    Dashboard dashboardFromRequest = createDashboard(3);
    dashboardFromRequest.getWidgets().get(2).setDashboardId(null);
    dashboardFromRequest.getWidgets().get(2).setId(null);

    Dashboard existedDashboard = createDashboard(2);
    Dashboard updatedDashboard = createDashboard(3);

    given(idGenerator.generateDashboardId()).willReturn(DASHBOARD_ID);
    given(idGenerator.generateWidgetId()).willReturn(WIDGET3_ID);
    given(dashboardRepository.findById(DASHBOARD_ID)).willReturn(Optional.of(existedDashboard));
    given(dashboardRepository.save(dashboardFromRequest)).willReturn(updatedDashboard);

    Long actual = dashboardService.updateDashboard(dashboardFromRequest);

    then(idGenerator).should().generateWidgetId();
    then(widgetRepository).should()
        .deleteAll(argThat(widgets -> ((List<Widget>) widgets).size() == 0));

    assertEquals(updatedDashboard.getId(), actual);
  }

  @Test
  void updateDashboard_shouldThrowResponseStatusException_whenWidgetIdsAreIncorrect() {
    Long incorrectWidgetId = 200L;
    Dashboard dashboardFromRequest = createDashboard(2);
    dashboardFromRequest.getWidgets().get(0).setId(incorrectWidgetId);

    Dashboard existedDashboard = createDashboard(2);

    given(dashboardRepository.findById(DASHBOARD_ID)).willReturn(Optional.of(existedDashboard));

    assertThrows(ResponseStatusException.class,
        () -> dashboardService.updateDashboard(dashboardFromRequest));
  }

  @Test
  void deleteDashboard_shouldDeleteDashboard_whenDashboardIsFound() {
    Dashboard dashboard = createDashboard(2);

    given(dashboardRepository.findById(DASHBOARD_ID)).willReturn(Optional.of(dashboard));

    dashboardService.deleteDashboard(dashboard.getId());

    then(dashboardRepository).should().delete(dashboard);
  }

  @Test
  void deleteDashboard_shouldThrowResponseStatusException_whenDashboardIsNotFound() {
    given(dashboardRepository.findById(DASHBOARD_ID)).willThrow(ResponseStatusException.class);

    assertThrows(ResponseStatusException.class,
        () -> dashboardService.deleteDashboard(DASHBOARD_ID));
  }

  private Dashboard createDashboard(int widgetsNumber) {
    Dashboard dashboard = new Dashboard();
    dashboard.setId(DASHBOARD_ID);
    dashboard.setTitle("Dashboard 1");
    dashboard.setDateRange(new DateRange(LocalDateTime.MIN, LocalDateTime.MAX));
    dashboard.setWidgets(new ArrayList<>());

    for (int i = 1; i <= widgetsNumber; i++) {
      Widget widget = new Widget();
      widget.setTitle("Widget " + i);
      widget.setType(WidgetType.values()[i]);
      widget.setId(100L + i);
      widget.setDashboardId(DASHBOARD_ID);
      dashboard.getWidgets().add(widget);
    }

    return dashboard;
  }

}
