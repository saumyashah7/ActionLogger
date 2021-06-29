package com.utsa.eager.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tokens")
public class Token {
	
	@Id
	@Column(name="token")
	private String value;
	
	private int userid;
	
	@Column(name="timestamp")
	private Timestamp expiretime;
	
	public Token() {
		
	}
	
	public Token(int userid, String value, Timestamp expiretime) {
		this.userid=userid;
		this.value=value;
		this.expiretime=expiretime;
	}	
	
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Timestamp getExpiretime() {
		return expiretime;
	}
	public void setExpiretime(Timestamp expiretime) {
		this.expiretime = expiretime;
	}

}

