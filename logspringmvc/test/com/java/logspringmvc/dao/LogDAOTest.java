package com.java.logspringmvc.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.java.logspringmvc.model.Log;

public class LogDAOTest {
	private DriverManagerDataSource datasource;
	private LogDAO dao;
	
	@Before
	public void setupBeforeEach()
	{
		datasource=new DriverManagerDataSource();
		datasource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		datasource.setUrl("jdbc:mysql://localhost:3306/test");
		datasource.setUsername("root");
		datasource.setPassword("");
		
		dao = new LogDAOImpl(datasource);
	}

	@Test
	public void testAddlog() {
		Log lg=new Log("date6","app6","method6","desc6");
		int result=dao.addlog(lg);
		assertTrue(result>0);
	}

	@Test
	public void testGetLogs() {
		List<Log> result=dao.getLogs();
		for(Log l:result)
			System.out.println(l);
		
	}

}
