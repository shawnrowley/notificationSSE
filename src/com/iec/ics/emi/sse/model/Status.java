package com.iec.ics.emi.sse.model;

import java.io.Serializable; 

@SuppressWarnings("serial")
public class Status implements Serializable{
	
	private String time;
	private String status;
	private int    code;

	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}

}


