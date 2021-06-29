package com.utsa.eager.model;

public class Log {
	
	String datetime;
	String application;
	String method;
	String description;
	
	public Log(String datetime, String application, String method, String description) {
		super();
		this.datetime = datetime;
		this.application = application;
		this.method = method;
		this.description = description;
	}
	@Override
	public String toString() {
		return "Log [datetime=" + datetime + ", application=" + application + ", method=" + method + ", description="
				+ description + "]";
	}
	public Log() {
		// TODO Auto-generated constructor stub
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}

