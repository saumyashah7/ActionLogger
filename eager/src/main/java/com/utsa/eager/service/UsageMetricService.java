package com.utsa.eager.service;

import java.util.List;
import com.utsa.eager.model.UsageMetric;

public interface UsageMetricService {
	
	public void updateUsage(UsageMetric usageMetric);
	public void incrementUsage(UsageMetric usageMetric);
	public List<UsageMetric> getAppUsage();

}
