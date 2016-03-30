package io.subutai.core.localpeer.cli;


import java.util.List;

import org.apache.karaf.shell.commands.Command;

import io.subutai.common.peer.LocalPeer;
import io.subutai.common.peer.NetworkResource;
import io.subutai.common.peer.PeerException;
import io.subutai.core.identity.rbac.cli.SubutaiShellCommandSupport;


@Command( scope = "localpeer", name = "list-net-resources" )
public class ListNetworkResourceCommand extends SubutaiShellCommandSupport
{

    private LocalPeer localPeer;


    public ListNetworkResourceCommand( final LocalPeer localPeer )
    {
        this.localPeer = localPeer;
    }


    @Override
    protected Object doExecute() throws Exception
    {
        try
        {
            List<NetworkResource> networkResources = localPeer.listReservedNetworkResources();

            System.out.format( "Found %d reserved network resources:%n", networkResources.size() );

            for ( NetworkResource networkResource : networkResources )
            {
                System.out.println( networkResource );
            }
        }
        catch ( PeerException e )
        {
            System.out.println( e.getMessage() );
        }
        return null;
    }
}
