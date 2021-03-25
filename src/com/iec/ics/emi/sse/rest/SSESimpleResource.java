package com.iec.ics.emi.sse.rest;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import com.iec.ics.emi.sse.model.SseEvent;
import com.iec.ics.emi.sse.model.Status;


@Path("/notification")
public class SSESimpleResource {


    private ExecutorService executor  = Executors.newSingleThreadExecutor();
	
	private static Set<SseEvent> allEventSinks = new HashSet<SseEvent>();
    
    static ScheduledExecutorService timer =
	        Executors.newSingleThreadScheduledExecutor();
    
    DateTimeFormatter timeFormatter = 
			DateTimeFormatter.ofPattern("HH:mm:ss");	
    

	@GET
	@Produces( MediaType.TEXT_PLAIN )
	@Path( "/isAlive" )
	public String isAlive( ) {
	   return "Alive";
	}
    

    @GET
    @Path("/eventStream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void eventStream(
            @Context SseEventSink eventSink,
            @Context Sse sse) {
   	
        /*	
    	eventSink.send(sse.newEventBuilder().data(String.class, "event1").build());
        eventSink.send(sse.newEventBuilder().data(String.class, "event2").build());
        eventSink.send(sse.newEventBuilder().data(String.class, "event3").build());
        */
    /*	executor.execute(() -> {
            try (SseEventSink sink = eventSink) {
                eventSink.send(sse.newEventBuilder().data(String.class, "event1").build());
                eventSink.send(sse.newEventBuilder().data(String.class, "event2").build());
                eventSink.send(sse.newEventBuilder().data(String.class, "event3").build());
            }
            catch (Throwable e) {
                e.printStackTrace(System.out);
            }
        }); */
    	
    	
        SseEvent event =  new SseEvent(eventSink, sse);
    	allEventSinks.add(event);
    	
    	if (allEventSinks.size()==1){
       		timer.scheduleAtFixedRate(
				  () ->sendStatusToAll(event),0,1,TimeUnit.SECONDS); 	
	   	}
	   	
    }
    
    private void sendStatusToAll(SseEvent event){
  	   
       	for (SseEvent ev: allEventSinks){
       		try {
		        if (!ev.getEventSink().isClosed()) {  
		        	
		        	Status status = new Status();
		        	status.setCode(500);
		        	status.setStatus("successful");
		        	status.setTime(LocalTime.now().format(timeFormatter));
		        	
		        	OutboundSseEvent outEvent = ev.getSse().newEventBuilder()
                            .mediaType(MediaType.APPLICATION_JSON_TYPE)
                            .data(Status.class, status)
                            .build();
		        	ev.getEventSink().send(outEvent);
		        	//ev.getEventSink().send(ev.getSse().newEvent("Local time: " + LocalTime.now().format(timeFormatter)));     
		        }	
    		}    
     		catch (Throwable e) {
    			e.printStackTrace(System.out);
    		}
       	}	
     }
}