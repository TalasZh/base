package io.subutai.core.peer.impl.dao;


import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import io.subutai.common.peer.ResourceHost;
import io.subutai.common.protocol.api.DataService;
import io.subutai.core.peer.impl.entity.ResourceHostEntity;


public class ResourceHostDataService implements DataService<String, ResourceHostEntity>
{
    private static final Logger LOG = LoggerFactory.getLogger( ResourceHostDataService.class );
    private EntityManagerFactory emf;
    //    private EntityManager em;


    public ResourceHostDataService( EntityManagerFactory entityManagerFactory )
    {
        this.emf = entityManagerFactory;
        //        this.em = entityManagerFactory.createEntityManager();
    }


    @Override
    public ResourceHostEntity find( final String id )
    {
        ResourceHostEntity result = null;
        EntityManager em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();
            result = em.find( ResourceHostEntity.class, id );
            em.getTransaction().commit();
        }
        catch ( Exception e )
        {
            LOG.error( e.toString(), e );
            if ( em.getTransaction().isActive() )
            {
                em.getTransaction().rollback();
            }
        }
        finally
        {
            em.close();
        }
        return result;
    }


    @Override
    public Collection<ResourceHostEntity> getAll()
    {
        Collection<ResourceHostEntity> result = Lists.newArrayList();
        EntityManager em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();
            result = em.createQuery( "select h from ResourceHostEntity h", ResourceHostEntity.class ).getResultList();
            em.getTransaction().commit();
        }
        catch ( Exception e )
        {
            LOG.error( e.toString(), e );
            if ( em.getTransaction().isActive() )
            {
                em.getTransaction().rollback();
            }
        }
        finally
        {
            em.close();
        }
        return result;
    }


    @Override
    public void persist( final ResourceHostEntity item )
    {
        EntityManager em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();
            em.persist( item );
            em.flush();
            em.getTransaction().commit();
        }
        catch ( Exception e )
        {
            LOG.error( e.toString(), e );
            if ( em.getTransaction().isActive() )
            {
                em.getTransaction().rollback();
            }
        }
        finally
        {
            em.close();
        }
    }


    @Override
    public void remove( final String id )
    {
        EntityManager em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();
            ResourceHostEntity item = em.find( ResourceHostEntity.class, id );
            em.remove( item );
            em.getTransaction().commit();
        }
        catch ( Exception e )
        {
            LOG.error( e.toString(), e );
            if ( em.getTransaction().isActive() )
            {
                em.getTransaction().rollback();
            }
        }
        finally
        {
            em.close();
        }
    }


    @Override
    public void update( ResourceHostEntity item )
    {
        EntityManager em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();
            em.merge( item );
            em.getTransaction().commit();
        }
        catch ( Exception e )
        {
            LOG.error( e.toString(), e );
            if ( em.getTransaction().isActive() )
            {
                em.getTransaction().rollback();
            }
        }
        finally
        {
            em.close();
        }
    }


    public void saveOrUpdate( ResourceHost item )
    {
        EntityManager em = emf.createEntityManager();

        try
        {

            em.getTransaction().begin();
            if ( em.find( ResourceHostEntity.class, item.getId() ) == null )
            {
                em.persist( item );
            }
            else
            {
                em.merge( item );
                em.refresh( item );
            }
            em.getTransaction().commit();
        }
        catch ( Exception e )
        {
            LOG.error( e.toString(), e );
            if ( em.getTransaction().isActive() )
            {
                em.getTransaction().rollback();
            }
        }
        finally
        {
            em.close();
        }
    }
}
