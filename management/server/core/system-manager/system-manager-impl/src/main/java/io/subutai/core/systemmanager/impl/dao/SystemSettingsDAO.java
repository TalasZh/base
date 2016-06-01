package io.subutai.core.systemmanager.impl.dao;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import io.subutai.common.dao.DaoManager;
import io.subutai.core.systemmanager.api.model.SecuritySettings;
import io.subutai.core.systemmanager.api.model.SystemSettings;
import io.subutai.core.systemmanager.impl.model.SecuritySettingsEntity;
import io.subutai.core.systemmanager.impl.model.SystemSettingsEntity;


/**
 *
 */
public class SystemSettingsDAO
{
    private static final Logger logger = LoggerFactory.getLogger( SystemSettingsDAO.class );
    private DaoManager daoManager;


    public SystemSettingsDAO( DaoManager daoManager )
    {
        this.daoManager = daoManager;
    }


    /* *************************************************
    *
    */
    public SystemSettings find( final String peerId )
    {
        EntityManager em = daoManager.getEntityManagerFromFactory();

        SystemSettings result = null;
        try
        {
            TypedQuery<SystemSettingsEntity> query =
                    em.createQuery( "select u from SystemSettingsEntity u", SystemSettingsEntity.class );
            //query.setParameter( "peerId", peerId );

            List<SystemSettingsEntity> users = query.getResultList();
            if ( users.size() > 0 )
            {
                result = users.iterator().next();
            }
        }
        catch ( Exception e )
        {
        }
        finally
        {
            daoManager.closeEntityManager( em );
        }
        return result;
    }


    /* *************************************************
     *
     */
    public List<SystemSettings> getAll()
    {
        EntityManager em = daoManager.getEntityManagerFromFactory();

        List<SystemSettings> result = Lists.newArrayList();
        try
        {
            result = em.createQuery( "select h from SystemSettingsEntity h", SystemSettings.class ).getResultList();
        }
        catch ( Exception e )
        {
        }
        finally
        {
            daoManager.closeEntityManager( em );
        }
        return result;
    }


    /* *************************************************
     *
     */
    public void persist( SystemSettings item )
    {
        EntityManager em = daoManager.getEntityManagerFromFactory();
        try
        {
            daoManager.startTransaction( em );
            em.persist( item );
            em.flush();

            daoManager.commitTransaction( em );
        }
        catch ( Exception e )
        {
            daoManager.rollBackTransaction( em );
        }
        finally
        {
            daoManager.closeEntityManager( em );
        }
    }


    /* *************************************************
     *
     */
    public void remove( final String id )
    {
        EntityManager em = daoManager.getEntityManagerFromFactory();
        try
        {
            daoManager.startTransaction( em );
            SecuritySettingsEntity item = em.find( SecuritySettingsEntity.class, id );
            em.remove( item );
            daoManager.commitTransaction( em );
        }
        catch ( Exception e )
        {
            daoManager.rollBackTransaction( em );
        }
        finally
        {
            daoManager.closeEntityManager( em );
        }
    }


    /* *************************************************
     *
     */
    public void update( final SystemSettings item )
    {
        EntityManager em = daoManager.getEntityManagerFromFactory();
        try
        {
            daoManager.startTransaction( em );
            em.merge( item );
            daoManager.commitTransaction( em );
        }
        catch ( Exception e )
        {
            logger.error("Error updating user", e);
            daoManager.rollBackTransaction( em );
        }
        finally
        {
            daoManager.closeEntityManager( em );
        }
    }

}
