package com.java.logspringmvc.model;

import java.sql.Timestamp;

public class Token {
	int userid;
	String value;
	Timestamp expiretime;
	
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
