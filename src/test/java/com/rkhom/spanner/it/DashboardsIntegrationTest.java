package com.rkhom.spanner.it;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.spanner.Statement;
import com.google.cloud.spring.data.spanner.core.SpannerTemplate;
import com.rkhom.spanner.utils.FileUtils;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class DashboardsIntegrationTest extends AbstractIntegrationTest {

  private static final Pattern DASHBOARD_ID_PATTERN = Pattern.compile("\\d+");

  private static final String EXISTING_DASHBOARD_ID = "1";
  private static final String MISSING_DASHBOARD_ID = "100500";

  private static final String DASHBOARD_SQL = "sql/dashboard.sql";

  private static final String DASHBOARDS_ENDPOINT = "/api/dashboards";
  private static final String DASHBOARDS_ENDPOINT_WITH_ID = "/api/dashboards/{dashboardId}";

  @Autowired
  private SpannerTemplate spannerTemplate;

  @BeforeEach
  void setUp() {
    createTables();
    insertDashboard();
  }

  @AfterEach
  void tearDown() {
    dropTables();
  }

  private void insertDashboard() {
    for (String dml : FileUtils.readFileAsString(DASHBOARD_SQL).split(QUERY_SEPARATOR)) {
      spannerTemplate.executeDmlStatement(Statement.of(dml));
    }
  }

  @Test
  void createDashboard_shouldReturnBadRequestStatus_whenRequestBodyIsInvalid() throws Exception {
    String requestBody = FileUtils.readFileAsString("request/create-dashboard-valid.json");

    mockMvc.perform(post(DASHBOARDS_ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().string(matchesPattern(DASHBOARD_ID_PATTERN)));
  }

  @Test
  void createDashboard_shouldCreateNewDashboardAndReturnId_whenRequestBodyIsValid()
      throws Exception {
    String requestBody = FileUtils.readFileAsString("request/create-dashboard-invalid.json");

    mockMvc.perform(post(DASHBOARDS_ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void updateDashboard_shouldUpdateExistingDashboard_whenRequestBodyIsValid() throws Exception {
    String requestBody = FileUtils.readFileAsString("request/update-dashboard-valid.json");

    mockMvc.perform(put(DASHBOARDS_ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().string(EXISTING_DASHBOARD_ID));
  }

  @Test
  void updateDashboard_shouldReturnBadRequestStatus_whenRequestBodyIsInvalid() throws Exception {
    String requestBody = FileUtils.readFileAsString("request/update-dashboard-invalid.json");

    mockMvc.perform(put(DASHBOARDS_ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void getDashboard_shouldReturnDashboard_whenDashboardExists() throws Exception {
    String responseBody = FileUtils.readFileAsString("response/get-dashboard.json");

    mockMvc.perform(get(DASHBOARDS_ENDPOINT_WITH_ID, EXISTING_DASHBOARD_ID))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().string(responseBody));
  }

  @Test
  void getDashboard_shouldReturnBadRequestStatus_whenDashboardDoesNotExist() throws Exception {
    mockMvc.perform(get(DASHBOARDS_ENDPOINT_WITH_ID, MISSING_DASHBOARD_ID))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void getAllDashboards_shouldReturnAllExistingDashboards() throws Exception {
    String responseBody = FileUtils.readFileAsString("response/get-all-dashboards.json");

    mockMvc.perform(get(DASHBOARDS_ENDPOINT))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().string(responseBody));
  }

  @Test
  void deleteDashboard_shouldDeleteDashboard_whenDashboardExists() throws Exception {
    mockMvc.perform(delete(DASHBOARDS_ENDPOINT_WITH_ID, EXISTING_DASHBOARD_ID))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void deleteDashboard_shouldReturnBadRequestStatus_whenDashboardDoesNotExist() throws Exception {
    mockMvc.perform(delete(DASHBOARDS_ENDPOINT_WITH_ID, MISSING_DASHBOARD_ID))
        .andExpect(status().is4xxClientError());
  }

}
