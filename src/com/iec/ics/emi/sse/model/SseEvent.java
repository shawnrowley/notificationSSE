package com.iec.ics.emi.sse.model;

import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import javax.ws.rs.sse.SseBroadcaster;

public class SseEvent {
	
	public SseEvent(SseEventSink eventSink, Sse sse) {
		this.eventSink = eventSink;
		this.sse = sse;
	}
	
	private SseEventSink eventSink;
	private Sse  sse;
	private SseBroadcaster broadcast; 
	
	public SseEventSink getEventSink() {
		return eventSink;
	}
	public void setEventSink(SseEventSink eventSink) {
		this.eventSink = eventSink;
	}
	public Sse getSse() {
		return sse;
	}
	public void setSse(Sse sse) {
		this.sse = sse;
	}
	public SseBroadcaster getBroadcast() {
		return broadcast;
	}
	public void setBroadcast(SseBroadcaster broadcast) {
		this.broadcast = broadcast;
	}

}
