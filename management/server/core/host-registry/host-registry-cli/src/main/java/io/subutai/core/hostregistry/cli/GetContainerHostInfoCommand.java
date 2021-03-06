package io.subutai.core.hostregistry.cli;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;

import com.google.common.base.Preconditions;

import io.subutai.common.host.ContainerHostInfo;
import io.subutai.core.hostregistry.api.HostDisconnectedException;
import io.subutai.core.hostregistry.api.HostRegistry;
import io.subutai.core.identity.rbac.cli.SubutaiShellCommandSupport;


@Command( scope = "host", name = "container-host", description = "Prints details about container host" )
public class GetContainerHostInfoCommand extends SubutaiShellCommandSupport
{
    private static final Logger LOG = LoggerFactory.getLogger( GetContainerHostInfoCommand.class.getName() );

    private final HostRegistry hostRegistry;

    @Argument( index = 0, name = "hostname or id", required = true, multiValued = false, description = "container "
            + "hostname or id" )
    String identifier;


    public GetContainerHostInfoCommand( final HostRegistry hostRegistry )
    {
        Preconditions.checkNotNull( hostRegistry );

        this.hostRegistry = hostRegistry;
    }


    @Override
    protected Object doExecute()
    {
        try
        {
            ContainerHostInfo containerHostInfo;

            try
            {
                containerHostInfo = hostRegistry.getContainerHostInfoById( identifier );
            }
            catch ( HostDisconnectedException e )
            {

                containerHostInfo = hostRegistry.getContainerHostInfoByHostname( identifier );
            }

            System.out.println( containerHostInfo );
        }
        catch ( HostDisconnectedException e )
        {
            System.out.println( "Host is not connected" );
            LOG.error( "Error in GetContainerHostInfoCommand", e );
        }

        return null;
    }
}
