package org.safehaus.subutai.core.peer.impl;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.safehaus.subutai.common.exception.HTTPException;
import org.safehaus.subutai.common.protocol.Agent;
import org.safehaus.subutai.common.util.JsonUtil;
import org.safehaus.subutai.common.util.RestUtil;
import org.safehaus.subutai.common.util.UUIDUtil;
import org.safehaus.subutai.core.agent.api.AgentManager;
import org.safehaus.subutai.core.container.api.ContainerCreateException;
import org.safehaus.subutai.core.container.api.ContainerManager;
import org.safehaus.subutai.core.db.api.DbManager;
import org.safehaus.subutai.core.peer.api.Peer;
import org.safehaus.subutai.core.peer.api.PeerContainer;
import org.safehaus.subutai.core.peer.api.PeerException;
import org.safehaus.subutai.core.peer.api.PeerManager;
import org.safehaus.subutai.core.peer.api.helpers.CloneContainersMessage;
import org.safehaus.subutai.core.peer.api.helpers.PeerCommand;
import org.safehaus.subutai.core.peer.api.message.Common;
import org.safehaus.subutai.core.peer.api.message.PeerMessageException;
import org.safehaus.subutai.core.peer.api.message.PeerMessageListener;
import org.safehaus.subutai.core.peer.impl.dao.PeerDAO;

import com.google.common.base.Strings;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;


/**
 * PeerManager implementation
 */
public class PeerManagerImpl implements PeerManager
{

    private static final Logger LOG = Logger.getLogger( PeerManagerImpl.class.getName() );
    private static final String SOURCE = "PEER_MANAGER";
    private final Queue<PeerMessageListener> peerMessageListeners = new ConcurrentLinkedQueue<>();
    private DbManager dbManager;
    private AgentManager agentManager;
    private PeerDAO peerDAO;
    private ContainerManager containerManager;
    private Set<PeerContainer> containers = new HashSet<>();


    public void init()
    {
        peerDAO = new PeerDAO( dbManager );
    }


    public void destroy()
    {
    }


    public void setDbManager( final DbManager dbManager )
    {
        this.dbManager = dbManager;
    }


    public void setAgentManager( final AgentManager agentManager )
    {
        this.agentManager = agentManager;
    }


    public ContainerManager getContainerManager()
    {
        return containerManager;
    }


    public void setContainerManager( final ContainerManager containerManager )
    {
        this.containerManager = containerManager;
    }


    @Override
    public boolean register( final Peer peer )
    {
        return peerDAO.saveInfo( SOURCE, peer.getId().toString(), peer );
    }


    @Override
    public UUID getSiteId()
    {
        return UUIDUtil.generateMACBasedUUID();
    }


    @Override
    public List<Peer> peers()
    {
        return peerDAO.getInfo( SOURCE, Peer.class );
    }


    @Override
    public boolean unregister( final String uuid )
    {
        return peerDAO.deleteInfo( SOURCE, uuid );
    }


    @Override
    public Peer getPeerByUUID( UUID uuid )
    {
        if ( getSiteId().compareTo( uuid ) == 0 )
        {
            Peer peer = new Peer();
            peer.setId( uuid );
            peer.setIp( getLocalIp() );
            peer.setName( "Me" );
            return peer;
        }

        return peerDAO.getInfo( SOURCE, uuid.toString(), Peer.class );
    }


    /*@Override
    public String getRemoteId( final String baseUrl )
    {
        RemotePeerClient client = new RemotePeerClient();
        client.setBaseUrl( baseUrl );
        return client.callRemoteRest();
    }*/


    @Override
    public void addPeerMessageListener( PeerMessageListener listener )
    {
        try
        {
            if ( !peerMessageListeners.contains( listener ) )
            {
                peerMessageListeners.add( listener );
            }
        }
        catch ( Exception ex )
        {
            LOG.log( Level.SEVERE, "Error in addPeerMessageListener", ex );
        }
    }


    @Override
    public void removePeerMessageListener( PeerMessageListener listener )
    {
        try
        {
            peerMessageListeners.remove( listener );
        }
        catch ( Exception ex )
        {
            LOG.log( Level.SEVERE, "Error in removePeerMessageListener", ex );
        }
    }


    @Override
    public String sendPeerMessage( final Peer peer, String recipient, final String message ) throws PeerMessageException
    {
        if ( peer == null )
        {
            throw new PeerMessageException( "Peer is null" );
        }
        if ( Strings.isNullOrEmpty( recipient ) )
        {
            throw new PeerMessageException( "Recipient is null or empty" );
        }
        if ( Strings.isNullOrEmpty( message ) )
        {
            throw new PeerMessageException( "Message is null or empty" );
        }

        try
        {
            if ( isPeerReachable( peer ) )
            {

                Map<String, String> params = new HashMap<>();
                params.put( Common.RECIPIENT_PARAM_NAME, recipient );
                params.put( Common.PEER_ID_PARAM_NAME, getSiteId().toString() );
                params.put( Common.MESSAGE_PARAM_NAME, message );
                try
                {
                    return RestUtil.post( String.format( Common.MESSAGE_REQUEST_URL, peer.getIp() ), params );
                }
                catch ( HTTPException e )
                {
                    LOG.log( Level.SEVERE, "Error in sendPeerMessage", e );
                    throw new PeerMessageException( e.getMessage() );
                }
            }
            else
            {
                String err = "Peer is not reachable";
                LOG.log( Level.SEVERE, "Error in sendPeerMessage", err );
                throw new PeerMessageException( err );
            }
        }
        catch ( PeerException e )
        {
            LOG.log( Level.SEVERE, "Error in sendPeerMessage", e );
            throw new PeerMessageException( e.getMessage() );
        }
    }


    @Override
    public String processPeerMessage( final String peerId, final String recipient, final String message )
            throws PeerMessageException
    {
        if ( Strings.isNullOrEmpty( peerId ) )
        {
            throw new PeerMessageException( "Peer id is null or empty" );
        }
        if ( Strings.isNullOrEmpty( recipient ) )
        {
            throw new PeerMessageException( "Recipient is null or empty" );
        }
        if ( Strings.isNullOrEmpty( message ) )
        {
            throw new PeerMessageException( "Message is null or empty" );
        }
        try
        {
            UUID peerUUID = UUID.fromString( peerId );
            Peer senderPeer = getPeerByUUID( peerUUID );
            if ( senderPeer != null )
            {
                try
                {
                    if ( isPeerReachable( senderPeer ) )
                    {
                        for ( PeerMessageListener listener : peerMessageListeners )
                        {
                            if ( listener.getName().equalsIgnoreCase( recipient ) )
                            {
                                try
                                {
                                    return listener.onMessage( senderPeer, message );
                                }
                                catch ( Exception e )
                                {
                                    LOG.log( Level.SEVERE, "Error in processPeerMessage", e );
                                    throw new PeerMessageException( e.getMessage() );
                                }
                            }
                        }
                        String err = String.format( "Recipient %s not found", recipient );
                        LOG.log( Level.SEVERE, "Error in processPeerMessage", err );
                        throw new PeerMessageException( err );
                    }
                    else
                    {
                        String err = String.format( "Peer is not reachable %s", senderPeer );
                        LOG.log( Level.SEVERE, "Error in processPeerMessage", err );
                        throw new PeerMessageException( err );
                    }
                }
                catch ( PeerException e )
                {
                    LOG.log( Level.SEVERE, "Error in processPeerMessage", e );
                    throw new PeerMessageException( e.getMessage() );
                }
            }
            else
            {
                String err = String.format( "Peer %s not found", peerId );
                LOG.log( Level.SEVERE, "Error in processPeerMessage", err );
                throw new PeerMessageException( err );
            }
        }
        catch ( IllegalArgumentException e )
        {
            LOG.log( Level.SEVERE, "Error in processPeerMessage", e );
            throw new PeerMessageException( e.getMessage() );
        }
    }


    @Override
    public boolean isPeerReachable( final Peer peer ) throws PeerException
    {
        if ( peer == null )
        {
            throw new PeerException( "Peer is null" );
        }
        if ( getPeerByUUID( peer.getId() ) != null )
        {

            if ( peer.getId().compareTo( getSiteId() ) == 0 )
            {
                return true;
            }
            try
            {
                RestUtil.get( String.format( Common.PING_URL, peer.getIp() ), null );
                return true;
            }
            catch ( HTTPException e )
            {
                return false;
            }
        }
        else
        {
            throw new PeerException( "Peer not found" );
        }
    }


    @Override
    public Set<Agent> getConnectedAgents( String environmentId ) throws PeerException
    {
        try
        {
            UUID envId = UUID.fromString( environmentId );
            return agentManager.getAgentsByEnvironmentId( envId );
        }
        catch ( IllegalArgumentException e )
        {
            LOG.log( Level.SEVERE, "Error in getConnectedAgents", e );
            throw new PeerException( e.getMessage() );
        }
    }


    @Override
    public Set<Agent> getConnectedAgents( final Peer peer, final String environmentId ) throws PeerException
    {
        if ( isPeerReachable( peer ) )
        {
            try
            {
                Map<String, String> params = new HashMap<>();
                params.put( Common.ENV_ID_PARAM_NAME, environmentId );
                String response = RestUtil.get( String.format( Common.GET_AGENTS_URL, peer.getIp() ), params );
                return JsonUtil.fromJson( response, new TypeToken<Set<Agent>>()
                {
                }.getType() );
            }
            catch ( JsonSyntaxException | HTTPException e )
            {
                LOG.log( Level.SEVERE, "Error in getConnectedAgents", e );
                throw new PeerException( e.getMessage() );
            }
        }
        else
        {
            String err = String.format( "Peer is not reachable %s", peer );
            LOG.log( Level.SEVERE, "Error in getConnectedAgents", err );
            throw new PeerException( err );
        }
    }


    @Override
    public Set<Agent> createContainers( CloneContainersMessage ccm )
    {
        UUID envId = ccm.getEnvId();
        String template = ccm.getTemplate();
        int numberOfNodes = ccm.getNumberOfNodes();
        String strategy = ccm.getStrategy();
        try
        {
            return containerManager.clone( envId, template, numberOfNodes, strategy, null );
        }
        catch ( ContainerCreateException e )
        {
            LOG.log( Level.SEVERE, e.getMessage() );
        }
        //TODO: replace with empty set;
        return null;
    }


    public boolean invoke( PeerCommand peerCommand ) throws PeerException
    {
        boolean result = false;
        UUID agentId = peerCommand.getPeerCommandMessage().getAgentId();
        PeerContainer container = findPeerContainer( agentId );
        if ( container == null )
        {
            throw new PeerException( String.format( "Container does not exist [%s]", agentId ) );
        }
        switch ( peerCommand.getType() )
        {
            case CLONE:
                break;
            case START:
                start( container );
                break;
            case STOP:
                stop( container );
                break;
            case ISCONNECTED:
                break;
            default:
                //TODO: log or exception?
        }
        return result;
    }


    private PeerContainer findPeerContainer( final UUID agentId )
    {
        PeerContainer result = null;
        Iterator iterator = containers.iterator();
        while ( result == null && iterator.hasNext() )
        {
            PeerContainer c = ( PeerContainer ) iterator.next();
            if ( c.getAgentId().equals( agentId ) )
            {
                result = c;
            }
        }
        return result;
    }


    private boolean start( PeerContainer container )
    {

        Agent physicalAgent = agentManager.getAgentByUUID( container.getParentHostId() );
        return containerManager.startLxcOnHost( physicalAgent, container.getHostname() );
    }


    private boolean stop( PeerContainer container )
    {

        Agent physicalAgent = agentManager.getAgentByUUID( container.getParentHostId() );
        return containerManager.stopLxcOnHost( physicalAgent, container.getHostname() );
    }


    private String getLocalIp()
    {
        Enumeration<NetworkInterface> n;
        try
        {
            n = NetworkInterface.getNetworkInterfaces();
            for (; n.hasMoreElements(); )
            {
                NetworkInterface e = n.nextElement();

                Enumeration<InetAddress> a = e.getInetAddresses();
                for (; a.hasMoreElements(); )
                {
                    InetAddress addr = a.nextElement();
                    if ( addr.getHostAddress().startsWith( "172" ) )
                    {
                        return addr.getHostAddress();
                    }
                }
            }
        }
        catch ( SocketException e )
        {
            LOG.severe( e.getMessage() );
        }


        return "127.0.0.1";
    }


    public Collection<PeerMessageListener> getPeerMessageListeners()
    {
        return Collections.unmodifiableCollection( peerMessageListeners );
    }
}