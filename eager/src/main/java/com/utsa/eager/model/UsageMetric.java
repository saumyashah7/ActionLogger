package com.utsa.eager.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "usage_metric")
@IdClass(UsageMetricId.class)
public class UsageMetric {
	
	@Id
	private int userid;
	
	@Id
	private String application;
	
	@Id
	private String metric;
	private int usage_data;
	
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public String getMetric() {
		return metric;
	}
	public void setMetric(String metric) {
		this.metric = metric;
	}	
	public int getUsage() {
		return usage_data;
	}
	public void setUsage(int usage) {
		this.usage_data = usage;
	}

}
