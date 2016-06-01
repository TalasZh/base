package io.subutai.core.systemmanager.impl.dao;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.subutai.common.dao.DaoManager;


/**
 *
 */
public class NetworkSettingsDAO
{
    private static final Logger logger = LoggerFactory.getLogger( NetworkSettingsDAO.class );
    private DaoManager daoManager;


    public NetworkSettingsDAO( DaoManager daoManager )
    {
        this.daoManager = daoManager;
    }
}
