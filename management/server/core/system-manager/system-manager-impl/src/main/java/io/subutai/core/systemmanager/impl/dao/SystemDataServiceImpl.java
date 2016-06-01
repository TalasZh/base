package io.subutai.core.systemmanager.impl.dao;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.subutai.common.dao.DaoManager;
import io.subutai.core.systemmanager.api.dao.SystemDataService;
import io.subutai.core.systemmanager.api.model.SecuritySettings;
import io.subutai.core.systemmanager.api.model.SystemSettings;


/**
 *
 */
public class SystemDataServiceImpl implements SystemDataService
{
    private static final Logger logger = LoggerFactory.getLogger( SystemDataServiceImpl.class );


    private DaoManager daoManager;
    private SecuritySettingsDAO securitySettingsDAO;
    private SystemSettingsDAO systemSettingsDAO;
    private NetworkSettingsDAO networkSettingsDAO;

    public SystemDataServiceImpl(DaoManager daoManager)
    {
        this.daoManager = daoManager;
        securitySettingsDAO = new SecuritySettingsDAO( this.daoManager );
        systemSettingsDAO = new SystemSettingsDAO( this.daoManager );
        networkSettingsDAO = new NetworkSettingsDAO( this.daoManager );
    }


    @Override
    public SecuritySettings getSecuritySettings( String peerId )
    {
        return securitySettingsDAO.find( peerId );
    }


    @Override
    public SystemSettings getSystemSettings( String peerId )
    {
        return systemSettingsDAO.find( peerId );
    }


    @Override
    public void saveSecuritySettings( SecuritySettings securitySettings )
    {
        try
        {
            securitySettingsDAO.update( securitySettings );
        }
        catch(Exception ex)
        {
            logger.error( "**** Error when saving Securitysettings " ,ex );
        }
    }


    @Override
    public void saveSystemSettings( SystemSettings systemSettings )
    {
        try
        {
            systemSettingsDAO.update( systemSettings );
        }
        catch(Exception ex)
        {
            logger.error( "**** Error when saving Systemsettings " ,ex );
        }
    }

}
