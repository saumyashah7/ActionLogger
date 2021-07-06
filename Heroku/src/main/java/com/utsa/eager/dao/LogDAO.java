package com.utsa.eager.dao;

import java.util.List;

import com.utsa.eager.model.Log;

public interface LogDAO {
	
	public int addlog(Log log);
	public List<Log> getLogs();

}
