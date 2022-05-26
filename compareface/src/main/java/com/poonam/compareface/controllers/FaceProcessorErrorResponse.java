package com.poonam.compareface.controllers;

/*
 * This class is response entity when a error is encountered
 * it conveys user HTTP status, error message,and timestamp when the error occurred.
 */
public class FaceProcessorErrorResponse {
	private int status;
	private String message;
	private long timeStamp;
	
	public FaceProcessorErrorResponse() {
	}
	public FaceProcessorErrorResponse(int status, String message, long timeStamp) {
		this.status = status;
		this.message = message;
		this.timeStamp = timeStamp;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
}
