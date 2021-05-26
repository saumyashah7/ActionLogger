package com.java.logspringmvc.dao;

import java.sql.Timestamp;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.java.logspringmvc.model.Token;

public class TokenDAOImpl implements TokenDAO{
	JdbcTemplate jdbcTemplate;
	
	public TokenDAOImpl(DataSource dataSource) {
	        jdbcTemplate = new JdbcTemplate(dataSource);
	    }
	
	
	@Override
	public String assignToken(int userid) {
		// TODO Auto-generated method stub

		int token_length=32;
		// chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
  
        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(token_length);
  
        for (int i = 0; i < token_length; i++) {
  
            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index = (int)(AlphaNumericString.length() * Math.random());
  
            // add Character one by one in end of sb
            sb.append(AlphaNumericString.charAt(index));
        }
     
		String query="insert into tokens values(?,?,?)";
		jdbcTemplate.update(query, userid, sb.toString(), new Timestamp(System.currentTimeMillis()));
  
        return sb.toString();
	}


	@Override
	public boolean verifyToken(Token token) {
		// TODO Auto-generated method stub
		
    	String sql = "SELECT count(*) FROM tokens where userid = ? AND token = ?";
		Integer count =jdbcTemplate.queryForObject(sql, 
				new Object[] {token.getUserid(),token.getValue()},
				Integer.class);
		
		if(count > 0) 
			{
				String query1="delete from tokens where userid = ? AND token = ?";
				jdbcTemplate.update(query1,token.getUserid(),token.getValue());
				return true;
			}
		
		return false;
	}

}
