package com.rkhom.spanner;

import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerPersistentEntityImpl;
import com.rkhom.spanner.model.Dashboard;
import com.rkhom.spanner.model.DateRange;
import com.rkhom.spanner.model.Widget;
import com.rkhom.spanner.service.DdlService;
import com.rkhom.spanner.utils.FileUtils;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.ClassTypeInformation;

public class DdlServiceTest {

  private static final String DDL_CREATION_FILE = "sql/schema.sql";

  private static final String DDL_DROPPING_FILE = "sql/cleanup.sql";

  @Mock
  private SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;

  @Mock
  private SpannerSchemaUtils spannerSchemaUtils;

  @Mock
  private SpannerMappingContext spannerMappingContext;

  @InjectMocks
  private DdlService ddlService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    var dateRangeEntity = new SpannerPersistentEntityImpl<>(
        ClassTypeInformation.from(DateRange.class));
    var dashboardEntity = new SpannerPersistentEntityImpl<>(
        ClassTypeInformation.from(Dashboard.class));
    var widgetEntity = new SpannerPersistentEntityImpl<>(
        ClassTypeInformation.from(Widget.class));

    given(spannerMappingContext.getPersistentEntities())
        .willReturn(List.of(dateRangeEntity, dashboardEntity, widgetEntity));

    given(spannerMappingContext.getPersistentEntity(DateRange.class))
        .willAnswer(invocation -> dateRangeEntity);
    given(spannerMappingContext.getPersistentEntity(Dashboard.class))
        .willAnswer(invocation -> dashboardEntity);
    given(spannerMappingContext.getPersistentEntity(Widget.class))
        .willAnswer(invocation -> widgetEntity);
  }

  @Test
  void createTables_shouldExecuteDdlCreationScripts_whenTablesDoNotExist() {
    List<String> ddl = List.of(FileUtils.readFileAsString(DDL_CREATION_FILE));

    given(spannerDatabaseAdminTemplate.tableExists(anyString())).willReturn(false);
    given(spannerSchemaUtils.getCreateTableDdlStringsForInterleavedHierarchy(Dashboard.class))
        .willReturn(ddl);

    ddlService.createTables();

    then(spannerDatabaseAdminTemplate).should().executeDdlStrings(ddl, true);
  }

  @Test
  void createTables_shouldDoNothing_whenTablesExist() {
    given(spannerDatabaseAdminTemplate.tableExists(anyString())).willReturn(true);

    ddlService.createTables();

    then(spannerDatabaseAdminTemplate).should(never()).executeDdlStrings(anyIterable(), eq(true));
  }

  @Test
  void dropTables_shouldDoNothing_whenTablesDoNotExist() {
    given(spannerDatabaseAdminTemplate.tableExists(anyString())).willReturn(false);

    ddlService.dropTables();

    then(spannerDatabaseAdminTemplate).should(never()).executeDdlStrings(anyIterable(), eq(false));
  }

  @Test
  void dropTables_shouldExecuteDdlDroppingScripts_whenTablesExist() {
    List<String> ddl = List.of(FileUtils.readFileAsString(DDL_DROPPING_FILE));

    given(spannerDatabaseAdminTemplate.tableExists(anyString())).willReturn(true);
    given(spannerSchemaUtils.getDropTableDdlStringsForInterleavedHierarchy(Dashboard.class))
        .willReturn(ddl);

    ddlService.dropTables();

    then(spannerDatabaseAdminTemplate).should().executeDdlStrings(ddl, false);
  }

}
