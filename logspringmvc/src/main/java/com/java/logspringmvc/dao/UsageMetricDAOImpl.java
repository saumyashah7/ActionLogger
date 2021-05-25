package com.java.logspringmvc.dao;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.java.logspringmvc.model.UsageMetric;
import com.java.logspringmvc.rowmapper.AppUsageMapper;

public class UsageMetricDAOImpl implements UsageMetricDAO{

	JdbcTemplate jdbcTemplate;
	
	public UsageMetricDAOImpl(DataSource dataSource) {
	        jdbcTemplate = new JdbcTemplate(dataSource);
	    }
	
	@Override
	public void updateUsage(UsageMetric usageMetric) {
		// TODO Auto-generated method stub
		String sql = "SELECT count(*) FROM usage_metric where userid = ? AND application = ? AND metric = ?";
		Integer count =jdbcTemplate.queryForObject(sql, 
				new Object[] {usageMetric.getUserid(),usageMetric.getApplication(),usageMetric.getMetric()},
				Integer.class);
		
		
		if(count > 0) 
		{
			String query1="update usage_metric set usage_data=? where userid = ? AND application = ? AND metric = ?";
			jdbcTemplate.update(query1, 
					new Object[] {usageMetric.getUsage(), usageMetric.getUserid(), usageMetric.getApplication(), usageMetric.getMetric()},
					new int[] {Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR}
			);
		}
		else
		{
			String query="insert into usage_metric values(?,?,?,?)";
			jdbcTemplate.update(query, usageMetric.getUserid(), usageMetric.getApplication(), usageMetric.getMetric(),usageMetric.getUsage());
			
		}	
	}

	@Override
	public List<UsageMetric> getAppUsage(){
		String sql = "select application,metric,sum(usage_data) as usagedata from usage_metric group by application,metric";
		List<UsageMetric> appusage = jdbcTemplate.query(sql, new AppUsageMapper());
		return appusage;
	}
}
