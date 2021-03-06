package io.subutai.core.environment.rest;


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.subutai.common.environment.Environment;
import io.subutai.common.environment.EnvironmentModificationException;
import io.subutai.common.environment.EnvironmentNotFoundException;
import io.subutai.common.environment.Topology;
import io.subutai.common.peer.EnvironmentContainerHost;
import io.subutai.common.settings.Common;
import io.subutai.core.environment.api.EnvironmentManager;
import io.subutai.core.environment.api.dto.ContainerDto;
import io.subutai.core.environment.api.dto.EnvironmentDto;
import io.subutai.core.environment.api.exception.EnvironmentCreationException;


/**
 * REST endpoint implementation of registration process
 */
public class RestServiceImpl implements RestService
{
    private static Logger LOG = LoggerFactory.getLogger( RestServiceImpl.class );

    private EnvironmentManager environmentManager;


    public RestServiceImpl( final EnvironmentManager environmentManager )
    {
        this.environmentManager = environmentManager;
    }


    @Override
    public Response createEnvironment( final Topology topology ) throws EnvironmentCreationException
    {
        try
        {
            Environment environment = environmentManager.createEnvironment( topology, true );

            return Response.ok( environment.getEnvironmentId() ).build();
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            return Response.serverError().build();
        }
    }


    @Override
    public Response growEnvironment( final String environmentId, final Topology topology )
            throws EnvironmentModificationException, EnvironmentNotFoundException
    {
        try
        {
            environmentManager.growEnvironment( environmentId, topology, true );

            return Response.ok().build();
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            return Response.serverError().build();
        }
    }


    @Override
    public Response listEnvironments()
    {
        try
        {
            Set<Environment> environments = environmentManager.getEnvironments();

            return Response.ok( environments ).build();
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            return Response.serverError().build();
        }
    }


    @Override
    public Response getEnvironment( final String environmentId )
    {
        try
        {
            Environment environment = environmentManager.loadEnvironment( environmentId );

            final Set<ContainerDto> containers = new HashSet<>();
            for ( EnvironmentContainerHost host : environment.getContainerHosts() )
            {
                containers.add( new ContainerDto( host.getId(), environmentId, host.getHostname(),
                        host.getInterfaceByName( Common.DEFAULT_CONTAINER_INTERFACE ).getIp(), host.getTemplateName(),
                        host.getContainerSize(), host.getArch().name(), host.getTags(), host.getPeerId(),
                        host.getResourceHostId().getId(), host.isLocal(), "subutai", host.getState() ) );
            }
            EnvironmentDto environmentDto =
                    new EnvironmentDto( environment.getId(), environment.getName(), environment.getStatus(), containers,
                            "subutai" );
            return Response.ok( environmentDto ).build();
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new WebApplicationException( e.getMessage() );
        }
    }

}
