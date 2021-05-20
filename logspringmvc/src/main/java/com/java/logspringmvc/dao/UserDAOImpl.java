package com.java.logspringmvc.dao;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.java.logspringmvc.model.User;

public class UserDAOImpl implements UserDAO{

	
	JdbcTemplate jdbcTemplate;
	
	public UserDAOImpl(DataSource dataSource) {
	        jdbcTemplate = new JdbcTemplate(dataSource);
	    }
	
	@Override
	public int addUser(String macadd) {
		// TODO Auto-generated method stub
		
		Integer count = this.jdbcTemplate.queryForObject("select count(*) from user where macaddress = ?", Integer.class, macadd);
		if(count>0)
			return jdbcTemplate.queryForObject("select userid from user where macaddress = ?", Integer.class, macadd);
		
		String query="INSERT INTO USER(MACADDRESS) VALUES(?)";
		jdbcTemplate.update(query, macadd);
		
		Integer id = this.jdbcTemplate.queryForObject("select userid from user where macaddress = ?", Integer.class, macadd);
		return id;
		
	}
	
	

}
