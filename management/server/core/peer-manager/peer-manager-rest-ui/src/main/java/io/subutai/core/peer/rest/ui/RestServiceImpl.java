package io.subutai.core.peer.rest.ui;


import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.subutai.common.host.ContainerHostState;
import io.subutai.common.host.HostInterfaces;
import io.subutai.common.metric.ProcessResourceUsage;
import io.subutai.common.metric.ResourceHostMetrics;
import io.subutai.common.network.Vni;
import io.subutai.common.peer.*;
import io.subutai.common.protocol.N2NConfig;
import io.subutai.common.protocol.Template;
import io.subutai.common.quota.DiskPartition;
import io.subutai.common.quota.DiskQuota;
import io.subutai.common.quota.RamQuota;
import io.subutai.common.security.utils.io.HexUtil;
import io.subutai.common.settings.ChannelSettings;
import io.subutai.common.util.JsonUtil;
import io.subutai.common.util.RestUtil;
import io.subutai.core.http.manager.api.HttpContextManager;
import io.subutai.core.peer.api.LocalPeer;
import io.subutai.core.peer.api.ManagementHost;
import io.subutai.core.peer.api.PeerManager;
import io.subutai.core.security.api.SecurityManager;
import io.subutai.core.security.api.crypto.EncryptionTool;
import io.subutai.core.security.api.crypto.KeyManager;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.form.Form;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class RestServiceImpl implements RestService
{

    private static final Logger LOGGER = LoggerFactory.getLogger( RestServiceImpl.class );
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private PeerManager peerManager;
    private HttpContextManager httpContextManager;
    protected JsonUtil jsonUtil = new JsonUtil();
    protected RestUtil restUtil = new RestUtil();
    private SecurityManager securityManager;


    public RestServiceImpl( final PeerManager peerManager, HttpContextManager httpContextManager,
                            SecurityManager securityManager )
    {
        this.peerManager = peerManager;
        this.httpContextManager = httpContextManager;
        this.securityManager = securityManager;
    }



    @Override
    public Response processRegisterRequest( String ip, String KeyPhrase )
    //public Response processRegisterRequest( String peer )
    {

        try
        {
            // ******* Convert HexString to Byte Array ****** Decrypt data
            EncryptionTool encTool = securityManager.getEncryptionTool();
            KeyManager keyManager = securityManager.getKeyManager();

            PeerInfo p = peerManager.getLocalPeerInfo();
            p.setKeyPhrase( KeyPhrase );
            PGPPublicKey pkey = keyManager.getRemoteHostPublicKey( null, ip );

            //************************************************

            if ( peerManager.getPeerInfo( p.getId() ) != null )
            {
                return Response.status( Response.Status.CONFLICT )
                        .entity(String.format("%s already registered", p.getName())).build();
            }
            else
            {
                PeerInfo localPeer = peerManager.getLocalPeerInfo();

                if ( pkey != null )
                {
                    localPeer.setKeyPhrase( p.getKeyPhrase() );
                    String jsonData = jsonUtil.to( localPeer );
                    byte[] data = encTool.encrypt( jsonData.getBytes(), pkey, false );

                    // Save to DB
                    p.setStatus( PeerStatus.REQUESTED );
                    p.setName( String.format( "Peer %s", p.getId() ) );
                    peerManager.register( p );

                    return Response.ok( HexUtil.byteArrayToHexString( data ) ).build();
                }
            }
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
        }
        catch ( Exception e )
        {
            LOGGER.error( "Error processing register request #processRegisterRequest", e );
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( e.toString() ).build();
        }
    }
}