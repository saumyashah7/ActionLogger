package com.utsa.eager.service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.utsa.eager.model.Token;
import com.utsa.eager.repository.TokenRepository;

@Service
public class TokenServiceImpl implements TokenService{
	
	@Autowired
	TokenRepository tokenRespository;

	@Override
	public String assignToken(int userid) {
		// TODO Auto-generated method stub
		
		Token token=tokenRespository.findByUserid(userid);
		if(token!=null)
			return token.getValue();
			
		//Generate token
		int token_length=32;
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
        
        // timestamp of expiration
        Timestamp timestamp= new Timestamp(new Date().getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        //cal.add(Calendar.HOUR, 24);
        cal.add(Calendar.MINUTE, 5);
        timestamp = new Timestamp(cal.getTime().getTime());

        tokenRespository.save(new Token(userid, sb.toString(), timestamp));
        return sb.toString();

	}

	@Override
	public boolean verifyToken(Token t) {
		// TODO Auto-generated method stub
		Optional<Token> tok=tokenRespository.findById(t.getValue());
		if(tok.isPresent() && tok.get().getUserid()==t.getUserid())
			return true;
		return false;
	}

}
