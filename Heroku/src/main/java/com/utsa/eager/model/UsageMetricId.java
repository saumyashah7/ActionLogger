package com.utsa.eager.model;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("serial")
public class UsageMetricId implements Serializable{
	
	private int userid;
	private String application;
	private String metric;
	
	public UsageMetricId() {
    }

    public UsageMetricId(int userid, String application, String metric) {
    	this.userid=userid;
        this.application = application;
        this.metric = metric;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsageMetricId umId = (UsageMetricId) o;
        return userid==umId.userid &&
        		application.equals(umId.application) &&
        		metric.equals(umId.metric);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid, application, metric);
    }
	

}
