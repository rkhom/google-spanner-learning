package com.rkhom.spanner.it;

import static java.util.stream.Collectors.toList;

import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import com.rkhom.spanner.utils.FileUtils;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.SpannerEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestSpannerConfiguration.class})
@ActiveProfiles("integration")
@Testcontainers
@DirtiesContext
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

  static final String QUERY_SEPARATOR = ";";

  static final String SPANNER_EMULATOR_IMAGE = "gcr.io/cloud-spanner-emulator/emulator:1.1.1";
  static final String SPANNER_EMULATOR_HOST = "spring.cloud.gcp.spanner.emulator-host";

  private static final String DDL_CREATION_FILE = "sql/schema.sql";
  private static final String DDL_DROPPING_FILE = "sql/cleanup.sql";

  @Container
  private static final SpannerEmulatorContainer spannerEmulator =
      new SpannerEmulatorContainer(DockerImageName.parse(SPANNER_EMULATOR_IMAGE));

  @DynamicPropertySource
  private static void emulatorProperties(DynamicPropertyRegistry registry) {
    registry.add(SPANNER_EMULATOR_HOST, spannerEmulator::getEmulatorGrpcEndpoint);
  }

  @Autowired
  MockMvc mockMvc;

  @Autowired
  SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;

  void createTables() {
    List<String> ddl = Stream
        .of(FileUtils.readFileAsString(DDL_CREATION_FILE).split(QUERY_SEPARATOR))
        .collect(toList());

    spannerDatabaseAdminTemplate.executeDdlStrings(ddl, true);
  }

  void dropTables() {
    List<String> ddl = Stream
        .of(FileUtils.readFileAsString(DDL_DROPPING_FILE).split(QUERY_SEPARATOR))
        .collect(toList());

    spannerDatabaseAdminTemplate.executeDdlStrings(ddl, false);
  }

}
