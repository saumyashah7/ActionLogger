package com.java.logspringmvc.dao;

import com.java.logspringmvc.model.Token;

public interface TokenDAO {
	
	public String assignToken(int userid);
	public boolean verifyToken(Token t);

}
