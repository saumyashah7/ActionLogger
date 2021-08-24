package com.utsa.eager.service;

import java.util.List;
import com.utsa.eager.model.UsageMetric;

public interface UsageMetricService {
	
	public void updateUsage(UsageMetric usageMetric);
	public void incrementUsage(UsageMetric usageMetric);
	public void incrementUsagebyCount(UsageMetric usageMetric, int count);
	public List<UsageMetric> getAppUsage();

}
