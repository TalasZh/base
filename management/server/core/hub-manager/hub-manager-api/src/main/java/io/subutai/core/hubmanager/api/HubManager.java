package io.subutai.core.hubmanager.api;


import java.util.Map;

import io.subutai.core.hubmanager.api.model.Config;
import io.subutai.hub.share.dto.SystemConfDto;


public interface HubManager
{
    void registerPeer( String hupIp, String email, String password ) throws Exception;

    void unregisterPeer() throws Exception;

    boolean isRegistered();

    void sendHeartbeat() throws Exception;

    void triggerHeartbeat();

    void processContainerEventProcessor() throws Exception;

    void sendResourceHostInfo() throws Exception;

    String getHubDns() throws Exception;

    String getProducts() throws Exception;

    void installPlugin( String url, String filename ) throws Exception;

    void uninstallPlugin( String name );



    Map<String, String> getPeerInfo() throws Exception;

    Config getHubConfiguration();

    String getChecksum();

    void sendSystemConfiguration( SystemConfDto dto );
}
