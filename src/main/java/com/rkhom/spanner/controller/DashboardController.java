package com.rkhom.spanner.controller;

import com.rkhom.spanner.model.Dashboard;
import com.rkhom.spanner.service.DashboardService;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboards")
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/{dashboardId}")
  public ResponseEntity<Dashboard> getDashboard(@PathVariable @NonNull Long dashboardId) {
    return ResponseEntity.ok(dashboardService.findDashboardById(dashboardId));
  }

  @GetMapping
  public ResponseEntity<List<Dashboard>> getAllDashboards() {
    return ResponseEntity.ok(dashboardService.findAllDashboards());
  }

  @PostMapping
  public ResponseEntity<Long> createDashboard(@RequestBody Dashboard dashboard) {
    return ResponseEntity.ok(dashboardService.createDashboard(dashboard));
  }

  @PutMapping
  public ResponseEntity<Long> updateDashboard(@RequestBody Dashboard dashboard) {
    return ResponseEntity.ok(dashboardService.updateDashboard(dashboard));
  }

  @DeleteMapping("/{dashboardId}")
  public ResponseEntity<?> deleteDashboard(@PathVariable @NonNull Long dashboardId) {
    dashboardService.deleteDashboard(dashboardId);
    return ResponseEntity.ok().build();
  }

}
