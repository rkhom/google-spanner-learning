package com.rkhom.spanner.it;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Instance;
import com.google.cloud.spanner.InstanceAdminClient;
import com.google.cloud.spanner.InstanceConfigId;
import com.google.cloud.spanner.InstanceId;
import com.google.cloud.spanner.InstanceInfo;
import com.google.cloud.spanner.InstanceNotFoundException;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spring.autoconfigure.spanner.GcpSpannerProperties;
import com.google.cloud.spring.data.spanner.core.admin.DatabaseIdProvider;
import com.google.spanner.admin.instance.v1.CreateInstanceMetadata;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestConfiguration
public class TestSpannerConfiguration {

  private static final String INSTANCE_CONFIG = "emulator-config";

  @Bean
  public CredentialsProvider googleCredentials() {
    return NoCredentialsProvider.create();
  }

  @Bean
  public Spanner spanner(GcpSpannerProperties spannerProperties) {
    return SpannerOptions.newBuilder()
        .setProjectId(spannerProperties.getProjectId())
        .setEmulatorHost(spannerProperties.getEmulatorHost())
        .build()
        .getService();
  }

  @Bean
  public DatabaseIdProvider spannerDatabase(Spanner spanner, GcpSpannerProperties spannerProperties)
      throws ExecutionException, InterruptedException {

    InstanceAdminClient instanceAdminClient = spanner.getInstanceAdminClient();
    InstanceId instanceId = InstanceId
        .of(spannerProperties.getProjectId(), spannerProperties.getInstanceId());

    Instance instance;
    try {
      instance = instanceAdminClient.getInstance(instanceId.getInstance());
    } catch (InstanceNotFoundException e) {
      // If instance doesn't exist, create a new Spanner instance in the emulator
      OperationFuture<Instance, CreateInstanceMetadata> operationFuture =
          instanceAdminClient.createInstance(
              InstanceInfo.newBuilder(instanceId)
                  // make sure to use the special `emulator-config`
                  .setInstanceConfigId(
                      InstanceConfigId.of(spannerProperties.getProjectId(), INSTANCE_CONFIG))
                  .build());
      instance = operationFuture.get();
    }

    instance.createDatabase(spannerProperties.getDatabase(), List.of()).get();

    return () -> DatabaseId.of(instanceId, spannerProperties.getDatabase());
  }
}
