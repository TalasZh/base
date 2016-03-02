package io.subutai.core.executor.rest;


import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public interface RestService
{

    @POST
    @Path( "heartbeat" )
    @Produces( { MediaType.TEXT_PLAIN } )
    Response processHeartbeat( @FormParam( "heartbeat" ) String heartbeat );

    @POST
    @Path( "response" )
    @Produces( { MediaType.TEXT_PLAIN } )
    Response processResponse( @FormParam( "response" ) String response );

    @GET
    @Path( "requests/{hostId}" )
    @Produces( { MediaType.APPLICATION_JSON } )
    Response getRequests( @PathParam( "hostId" ) String hostId );
}
