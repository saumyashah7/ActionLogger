package com.java.logspringmvc.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.java.logspringmvc.model.UsageMetric;

public class AppUsageMapper implements RowMapper<UsageMetric> {
	   public UsageMetric mapRow(ResultSet rs, int rowNum) throws SQLException {
		   UsageMetric um = new UsageMetric();
		   um.setApplication(rs.getString("application"));
		   um.setMetric(rs.getString("metric"));
		   um.setUsage(rs.getInt("usagedata"));
		   return um;
		   }
}


