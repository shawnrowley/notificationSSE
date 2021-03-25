package com.iec.ics.emi.sse.rest;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import com.iec.ics.emi.sse.model.SseEvent;
import com.iec.ics.emi.sse.model.Status;

@Path("/notification")
public class SSENotificationResource {
	
	
    private static Set<SseEvent> eventSinks = new HashSet<SseEvent>();
    
    static ScheduledExecutorService timer =
	        Executors.newSingleThreadScheduledExecutor();
    
    DateTimeFormatter timeFormatter = 
			DateTimeFormatter.ofPattern("HH:mm:ss");	

    @Context Sse sse;
    private static SseBroadcaster sseBroadcaster;

    @PostConstruct
    public void postConstruct() {
        getBroadcaster(sse);
    }

    private static SseBroadcaster getBroadcaster(Sse sse) {
        if (null == sseBroadcaster) {
            sseBroadcaster = sse.newBroadcaster();
        }
        return sseBroadcaster;
    }

    @GET
    @Path("/subscribe")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void subscribe(@Context SseEventSink eventSink, @Context Sse client_sse) {
        eventSink.send(sse.newEvent("Connected!"));
        getBroadcaster(sse).register(eventSink);
        
        SseEvent event =  new SseEvent(eventSink, client_sse);
    	eventSinks.add(event);
    	
    	if (eventSinks.size()==1){
       		timer.scheduleAtFixedRate(
				  () ->sendStatusToAll(event),0,1,TimeUnit.SECONDS); 	
	   	}
    }
 
    @POST
    @Path("/broadcast")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void broadcast(@FormParam("data") String event) {
        getBroadcaster(sse).broadcast(sse.newEventBuilder().data(String.class, event).build());
    }
    
    
    private void sendStatusToAll(SseEvent event){
    	
    	Status status = new Status();
    	status.setCode(500);
    	status.setStatus("successful");
    	status.setTime(LocalTime.now().format(timeFormatter));
    	
    	OutboundSseEvent outEvent = sse.newEventBuilder()
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(Status.class, status)
                .build();
    	getBroadcaster(sse).broadcast(outEvent);
    	
     	/*for (SseEvent ev: eventSinks){
     		try (SseEventSink sink = ev.getEventSink()) {
		        if (!ev.getEventSink().isClosed()) {  
		        	getBroadcaster(sse).broadcast(sse.newEvent("Local time: " + LocalTime.now().format(timeFormatter)));
		        //	ev.getEventSink().send(ev.getSse().newEvent("Local time: " + LocalTime.now().format(timeFormatter)));     
		        }	
    		}    
     		catch (Throwable e) {
    			e.printStackTrace(System.out);
    		}
       	}	*/
     }
}

