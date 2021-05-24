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
		String sql = "SELECT count(*) FROM usage_metric where userid = ? AND application = ?";
		Integer count =jdbcTemplate.queryForObject(sql, 
				new Object[] {usageMetric.getUserid(),usageMetric.getApplication()},
				Integer.class);
		
		
		if(count > 0) 
		{
//			String query = "SELECT usage_data FROM usage_metric where userid = ? AND application = ?";
//			Integer usage = jdbcTemplate.queryForObject(query, 
//					new Object[] {usageMetric.getUserid(),usageMetric.getApplication()},
//					Integer.class);
			
			
//			usage+=usageMetric.getUsage();
			String query1="update usage_metric set usage_data=? where userid = ? AND application = ?";
			jdbcTemplate.update(query1, 
					new Object[] {usageMetric.getUsage(), usageMetric.getUserid(), usageMetric.getApplication()},
					new int[] {Types.INTEGER, Types.INTEGER, Types.VARCHAR}
			);
		}
		else
		{
			String query="insert into usage_metric values(?,?,?)";
			jdbcTemplate.update(query, usageMetric.getUserid(), usageMetric.getApplication(), usageMetric.getUsage());
			
		}	
	}

	@Override
	public List<UsageMetric> getAppUsage(){
		String sql = "select application,sum(usage_data) as usagedata from usage_metric group by application";
		List<UsageMetric> appusage = jdbcTemplate.query(sql, new AppUsageMapper());
		return appusage;
	}
}
