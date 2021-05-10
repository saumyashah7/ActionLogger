package com.java.logspringmvc.dao;

import java.util.List;

import com.java.logspringmvc.model.Log;

public interface LogDAO {
	
	public int addlog(Log log);
	public List<Log> getLogs();

}
