package io.subutai.core.systemmanager.api;


import org.apache.commons.configuration.ConfigurationException;

import io.subutai.core.systemmanager.api.model.SecuritySettings;
import io.subutai.core.systemmanager.api.pojo.AdvancedSettings;
import io.subutai.core.systemmanager.api.pojo.KurjunSettings;
import io.subutai.core.systemmanager.api.pojo.NetworkSettings;
import io.subutai.core.systemmanager.api.pojo.SystemInfo;


public interface SystemManager
{

    KurjunSettings getKurjunSettings() throws ConfigurationException;


    NetworkSettings getNetworkSettings() throws ConfigurationException;

    SystemInfo getSystemInfo();


    void setNetworkSettings( String securePortX1, String securePortX2, String securePortX3, final String publicUrl,
                             final String agentPort, final String publicSecurePort, final String keyServer ) throws ConfigurationException;

    AdvancedSettings getAdvancedSettings();

    void setKurjunSettingsUrls( String[] globalKurjunUrls, final String[] localKurjunUrls )
            throws ConfigurationException;

    boolean setKurjunSettingsQuotas( long publicDiskQuota, long publicThreshold, long publicTimeFrame,
                                     long trustDiskQuota, long trustThreshold, long trustTimeFrame );

    void sendSystemConfigToHub() throws ConfigurationException;

    SystemInfo getManagementUpdates();


    boolean updateManagement();


    io.subutai.core.systemmanager.api.model.SystemSettings getSystemSettings( String peerId );


    void updateSystemSettings( String peerId );

    io.subutai.core.systemmanager.api.model.SystemSettings saveSystemSettings( String peerId, String peerOwnerId );


    void saveSystemSettings( io.subutai.core.systemmanager.api.model.SystemSettings systemSettings );


    SecuritySettings getSecuritySettings( String peerId );


    SecuritySettings saveSecuritySettings( String peerId, String secretPwd );


    void saveSecuritySettings( SecuritySettings securitySettings );
}
