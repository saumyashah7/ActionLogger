package com.utsa.eager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.utsa.eager.model.UsageMetric;
import com.utsa.eager.model.UsageMetricId;

@Repository
public interface UsageMetricRepository extends JpaRepository<UsageMetric, UsageMetricId>{
	
	@Query(value="select count(um.userid) as userid,um.application,um.metric,sum(um.usage_data) as usage_data from usage_metric um group by um.application,um.metric",nativeQuery = true)
	public List<UsageMetric> findAll();

}
