package com.rkhom.spanner.it;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

public class DdlIntegrationTest extends AbstractIntegrationTest {

  private static final String DDL_ENDPOINT = "/api/ddl";

  private static final String DASHBOARDS_TABLE = "dashboards";
  private static final String WIDGETS_TABLE = "widgets";

  @Test
  void createTablesEndpoint_shouldCreateTables() throws Exception {
    mockMvc.perform(post(DDL_ENDPOINT)).andExpect(status().is2xxSuccessful()).andReturn();

    assertTrue(spannerDatabaseAdminTemplate.tableExists(DASHBOARDS_TABLE));
    assertTrue(spannerDatabaseAdminTemplate.tableExists(WIDGETS_TABLE));

    dropTables();
  }

  @Test
  void dropTablesEndpoint_shouldDropTables() throws Exception {
    createTables();

    mockMvc.perform(delete(DDL_ENDPOINT)).andExpect(status().is2xxSuccessful()).andReturn();

    assertFalse(spannerDatabaseAdminTemplate.tableExists(DASHBOARDS_TABLE));
    assertFalse(spannerDatabaseAdminTemplate.tableExists(WIDGETS_TABLE));
  }

}
