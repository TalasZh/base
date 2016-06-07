package io.subutai.core.hubmanager.impl.environment.state;


import io.subutai.common.peer.LocalPeer;
import io.subutai.core.environment.api.EnvironmentManager;
import io.subutai.core.hubmanager.api.HubManager;
import io.subutai.core.hubmanager.impl.http.HubRestClient;
import io.subutai.core.hubmanager.impl.processor.EnvironmentUserHelper;
import io.subutai.core.identity.api.IdentityManager;


public class Context
{
    public final IdentityManager identityManager;

    public final EnvironmentManager envManager;

    public final EnvironmentUserHelper envUserHelper;

    public final LocalPeer localPeer;

    public final HubRestClient restClient;

    public final HubManager hubManager;


    public Context( IdentityManager identityManager, EnvironmentManager envManager, EnvironmentUserHelper envUserHelper,
                    LocalPeer localPeer, HubRestClient restClient, HubManager hubManager )
    {
        this.identityManager = identityManager;

        this.envManager = envManager;

        this.envUserHelper = envUserHelper;

        this.localPeer = localPeer;

        this.restClient = restClient;

        this.hubManager = hubManager;
    }
}
