package com.rkhom.spanner.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {

  private static final String BASE_PACKAGE = "com.rkhom.spanner.controller";
  public static final String DASHBOARD_CONTROLLER = "Dashboard Controller";
  public static final String DDL_CONTROLLER = "Spanner DDL Controller";

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
        .paths(PathSelectors.any())
        .build()
        .tags(
            new Tag(DASHBOARD_CONTROLLER, "Simple CRUD operations with dashboards"),
            new Tag(DDL_CONTROLLER, "Create and remove tables in Spanner database"));
  }

}
