package com.rkhom.spanner.service;

import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import com.google.cloud.spring.data.spanner.core.mapping.Interleaved;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerMappingContext;
import com.google.cloud.spring.data.spanner.core.mapping.SpannerPersistentEntity;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.stereotype.Service;

/**
 * Auxiliary service for performing DDL operations with Spanner database. DDL scripts for table
 * creation and dropping are being generated on the fly according to entity property mappings.
 */
@Service
@RequiredArgsConstructor
public class DdlService {

  private final SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;

  private final SpannerSchemaUtils spannerSchemaUtils;

  private final SpannerMappingContext spannerMappingContext;

  public void createTables() {
    Set<String> interleavedTables = getInterleavedTableNames();

    List<String> creationDdls = spannerMappingContext.getPersistentEntities().stream()
        .filter(entity -> !interleavedTables.contains(entity.tableName()))
        .filter(entity -> !spannerDatabaseAdminTemplate.tableExists(entity.tableName()))
        .map(PersistentEntity::getType)
        .map(spannerSchemaUtils::getCreateTableDdlStringsForInterleavedHierarchy)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());

    spannerDatabaseAdminTemplate.executeDdlStrings(creationDdls, true);
  }

  public void dropTables() {
    Set<String> interleavedTables = getInterleavedTableNames();

    List<String> droppingDdls = spannerMappingContext.getPersistentEntities().stream()
        .filter(entity -> !interleavedTables.contains(entity.tableName()))
        .filter(entity -> spannerDatabaseAdminTemplate.tableExists(entity.tableName()))
        .map(PersistentEntity::getType)
        .map(spannerSchemaUtils::getDropTableDdlStringsForInterleavedHierarchy)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());

    spannerDatabaseAdminTemplate.executeDdlStrings(droppingDdls, false);
  }

  private Set<String> getInterleavedTableNames() {
    return spannerMappingContext.getPersistentEntities().stream()
        .flatMap(this::getChildEntities)
        .map(SpannerPersistentEntity::tableName)
        .collect(Collectors.toSet());
  }

  private Stream<SpannerPersistentEntity<?>> getChildEntities(
      SpannerPersistentEntity<?> parentEntity) {

    return StreamSupport
        .stream(parentEntity.getPersistentProperties(Interleaved.class).spliterator(), false)
        .map(PersistentProperty::getActualType)
        .map(spannerMappingContext::getPersistentEntity);
  }

}
