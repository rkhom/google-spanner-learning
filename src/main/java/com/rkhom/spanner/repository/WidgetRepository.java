package com.rkhom.spanner.repository;

import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import com.rkhom.spanner.model.Widget;
import org.springframework.stereotype.Repository;

@Repository
public interface WidgetRepository extends SpannerRepository<Widget, Long> {

}
