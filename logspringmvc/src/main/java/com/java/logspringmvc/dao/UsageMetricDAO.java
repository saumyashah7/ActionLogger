package com.java.logspringmvc.dao;

import java.util.List;

import com.java.logspringmvc.model.UsageMetric;

public interface UsageMetricDAO {
	
	public void updateUsage(UsageMetric usageMetric);
	public void incrementUsage(UsageMetric usageMetric);
	public List<UsageMetric> getAppUsage();

}
