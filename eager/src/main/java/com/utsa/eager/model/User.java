package com.utsa.eager.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {
	
	@Id
	@Column(name="userid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;
	
	String machineaddress;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getMachineaddress() {
		return machineaddress;
	}
	public void setMachineaddress(String macaddress) {
		this.machineaddress = macaddress;
	}
	

}
