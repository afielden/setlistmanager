package com.bigdrum.setlistmanager.database;

public class DataServiceException extends Exception {
	private static final long serialVersionUID = -5892896302202216030L;
	String reason;
	Throwable exception;
	
	public DataServiceException(String reason) {
		this.reason = reason;
	}
	
	public DataServiceException(String reason, Throwable exception) {
		this.reason = reason;
		this.exception = exception;
	}
	
	public String getReason() {
		return reason;
	}
	
	public Throwable getException() {
		return exception;
	}
}
