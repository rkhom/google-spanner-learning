package com.rkhom.spanner.service;

import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import com.google.cloud.spring.data.spanner.core.mapping.Embedded;
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
    Set<Class<?>> interleavedEntities = getInterleavedEntityClasses();
    Set<Class<?>> embeddedEntities = getEmbeddedEntityClasses();

    List<String> creationDdls = spannerMappingContext.getPersistentEntities().stream()
        .filter(entity -> !interleavedEntities.contains(entity.getType()))
        .filter(entity -> !embeddedEntities.contains(entity.getType()))
        .filter(entity -> !spannerDatabaseAdminTemplate.tableExists(entity.tableName()))
        .map(PersistentEntity::getType)
        .map(spannerSchemaUtils::getCreateTableDdlStringsForInterleavedHierarchy)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());

    if (creationDdls.isEmpty()) {
      return;
    }

    spannerDatabaseAdminTemplate.executeDdlStrings(creationDdls, true);
  }

  public void dropTables() {
    Set<Class<?>> interleavedEntities = getInterleavedEntityClasses();

    List<String> droppingDdls = spannerMappingContext.getPersistentEntities().stream()
        .filter(entity -> !interleavedEntities.contains(entity.getType()))
        .filter(entity -> spannerDatabaseAdminTemplate.tableExists(entity.tableName()))
        .map(PersistentEntity::getType)
        .map(spannerSchemaUtils::getDropTableDdlStringsForInterleavedHierarchy)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());

    if (droppingDdls.isEmpty()) {
      return;
    }

    spannerDatabaseAdminTemplate.executeDdlStrings(droppingDdls, false);
  }

  private Set<Class<?>> getInterleavedEntityClasses() {
    return spannerMappingContext.getPersistentEntities().stream()
        .flatMap(this::getInterleavedEntities)
        .map(SpannerPersistentEntity::getType)
        .collect(Collectors.toSet());
  }

  private Stream<SpannerPersistentEntity<?>> getInterleavedEntities(
      SpannerPersistentEntity<?> parentEntity) {

    return StreamSupport
        .stream(parentEntity.getPersistentProperties(Interleaved.class).spliterator(), false)
        .map(PersistentProperty::getActualType)
        .map(spannerMappingContext::getPersistentEntity);
  }

  private Set<Class<?>> getEmbeddedEntityClasses() {
    return spannerMappingContext.getPersistentEntities().stream()
        .flatMap(this::getEmbeddedEntities)
        .map(SpannerPersistentEntity::getType)
        .collect(Collectors.toSet());
  }

  private Stream<SpannerPersistentEntity<?>> getEmbeddedEntities(
      SpannerPersistentEntity<?> entity) {

    return StreamSupport
        .stream(entity.getPersistentProperties(Embedded.class).spliterator(), false)
        .map(PersistentProperty::getActualType)
        .map(spannerMappingContext::getPersistentEntity);
  }

}
