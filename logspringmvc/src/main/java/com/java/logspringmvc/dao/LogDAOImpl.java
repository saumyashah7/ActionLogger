package com.java.logspringmvc.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.java.logspringmvc.model.Log;

public class LogDAOImpl implements LogDAO {

	private JdbcTemplate jdbcTemplate;
	 
    public LogDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
 
	@Override
	public int addlog(Log log) {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO logs (datetime, application, method, description)" + " VALUES (?, ?, ?, ?)";
		return jdbcTemplate.update(sql, log.getDatetime(),log.getApplication(), log.getMethod(), log.getDescription());		
	}

	@Override
	public List<Log> getLogs() {
		// TODO Auto-generated method stub
		String sql="select * from logs";
		List<Log> logs = jdbcTemplate.query(sql, new RowMapper<Log>() {
			 
	        @Override
	        public Log mapRow(ResultSet rs, int rowNum) throws SQLException {
	        	Log log = new Log();
	        	log.setDatetime(rs.getString("datetime"));
	        	log.setApplication(rs.getString("application"));
	        	log.setMethod(rs.getString("method"));
	        	log.setDescription(rs.getString("description"));
	            return log;
	        }
	 
	    });
	    return logs; 
		
	}

}
