package com.utsa.eager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.utsa.eager.model.Token;


@Repository
public interface TokenRepository extends JpaRepository<Token, String>{

	Token findByUserid(int userid);
	
}
