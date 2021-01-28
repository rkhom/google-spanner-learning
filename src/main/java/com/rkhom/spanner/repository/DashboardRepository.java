package com.rkhom.spanner.repository;

import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import com.rkhom.spanner.model.Dashboard;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardRepository extends SpannerRepository<Dashboard, Long> {

}
