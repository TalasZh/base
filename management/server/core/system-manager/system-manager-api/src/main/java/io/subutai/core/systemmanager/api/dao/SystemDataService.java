package io.subutai.core.systemmanager.api.dao;


import io.subutai.core.systemmanager.api.model.SecuritySettings;
import io.subutai.core.systemmanager.api.model.SystemSettings;


/**
 *
 */
public interface SystemDataService
{
    SecuritySettings getSecuritySettings( String peerId );

    SystemSettings getSystemSettings( String peerId );

    void saveSecuritySettings( SecuritySettings securitySettings );

    void saveSystemSettings( SystemSettings systemSettings );
}
