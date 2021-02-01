package com.rkhom.spanner.repository;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository<T, ID> {

  Optional<T> findById(ID id);

  List<T> findAll();

}
