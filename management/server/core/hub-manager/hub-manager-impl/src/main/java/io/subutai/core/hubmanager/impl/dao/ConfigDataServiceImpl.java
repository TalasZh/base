package io.subutai.core.hubmanager.impl.dao;


import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.subutai.common.dao.DaoManager;
import io.subutai.core.hubmanager.api.dao.ConfigDataService;
import io.subutai.core.hubmanager.api.model.Config;
import io.subutai.core.hubmanager.impl.model.ConfigEntity;


public class ConfigDataServiceImpl implements ConfigDataService
{
    private static final Logger LOG = LoggerFactory.getLogger( ConfigDataServiceImpl.class );
    private DaoManager daoManager;


    public ConfigDataServiceImpl( final DaoManager daoManager )
    {
        this.daoManager = daoManager;
    }


    @Override
    public void saveHubConfig( final Config config )
    {
        EntityManager em = daoManager.getEntityManagerFactory().createEntityManager();


        try
        {
            daoManager.startTransaction( em );
            em.merge( config );
            daoManager.commitTransaction( em );
        }
        catch ( Exception ex )
        {
            daoManager.rollBackTransaction( em );
            LOG.error( "ConfigDataService saveConfig:" + ex.toString() );
        }
        finally
        {
            daoManager.closeEntityManager( em );
        }
    }


    @Override
    public ConfigEntity getHubConfig( String peerId )
    {
        EntityManager em = daoManager.getEntityManagerFactory().createEntityManager();

        try
        {
            ConfigEntity configuration = em.find( ConfigEntity.class, peerId );
            return configuration;
        }
        catch ( Exception ex )
        {
            LOG.error( "ConfigDataService getConfig:" + ex.toString() );
            return null;
        }
        finally
        {
            daoManager.closeEntityManager( em );
        }
    }


    @Override
    public void deleteConfig( final String peerId )
    {
        EntityManager em = daoManager.getEntityManagerFromFactory();

        try
        {
            daoManager.startTransaction( em );
            ConfigEntity entity = em.find( ConfigEntity.class, peerId );
            em.remove( entity );
            em.flush();
            daoManager.commitTransaction( em );
        }
        catch ( Exception ex )
        {
            daoManager.rollBackTransaction( em );
            LOG.error( "ConfigDataService deleteOperation:" + ex.toString() );
        }
        finally
        {
            daoManager.closeEntityManager( em );
        }
    }
}
