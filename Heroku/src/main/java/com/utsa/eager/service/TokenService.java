package com.utsa.eager.service;

import com.utsa.eager.model.Token;

public interface TokenService {
	
	public String assignToken(int userid);
	public boolean verifyToken(Token t);

}
