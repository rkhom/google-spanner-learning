package com.rkhom.spanner.controller;

import com.rkhom.spanner.configuration.SwaggerConfiguration;
import com.rkhom.spanner.service.DdlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ddl")
@Api(tags = SwaggerConfiguration.DDL_CONTROLLER)
public class DdlController {

  private final DdlService ddlService;

  @PostMapping
  @ApiOperation(value = "Create tables")
  public ResponseEntity<?> createTables() {
    ddlService.createTables();
    return ResponseEntity.ok().build();
  }

  @DeleteMapping
  @ApiOperation(value = "Drop tables")
  public ResponseEntity<?> dropTables() {
    ddlService.dropTables();
    return ResponseEntity.ok().build();
  }

}
