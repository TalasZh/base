package io.subutai.core.kurjun.manager.rest;


import java.util.List;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.ConfigurationException;

import com.google.common.base.Strings;

import io.subutai.common.util.JsonUtil;
import io.subutai.core.kurjun.manager.api.KurjunManager;
import io.subutai.core.kurjun.manager.api.model.Kurjun;


public class RestServiceImpl implements RestService
{
    private static final Logger LOG = LoggerFactory.getLogger( RestServiceImpl.class.getName() );

    private KurjunManager kurjunManager;


    @Override
    public Response getKurjunUrl()
    {
        List<Kurjun> urls = kurjunManager.getDataService().getAllKurjunData();

        String info = JsonUtil.GSON.toJson( urls );

        return Response.status( Response.Status.OK ).entity( info ).build();
    }


    @Override
    public Response register( final String id )
    {
        String authId = "";
        if ( Strings.isNullOrEmpty( kurjunManager.getUser( Integer.parseInt( id ) ) ) )
        {
            authId = kurjunManager.registerUser( Integer.parseInt( id ) );
        }
        else
        {
            authId = kurjunManager.getDataService().getKurjunData( Integer.parseInt( id ) ).getAuthID();
        }

        return Response.status( Response.Status.OK ).entity( authId ).build();
    }


    @Override
    public Response update( final String id, final String url )
    {
        try
        {
            kurjunManager.updateUrl( Integer.parseInt( id ), url );
            return Response.status( Response.Status.OK ).build();
        }
        catch ( ConfigurationException e )
        {
            LOG.error( "Error in saving URL:" + e.getMessage() );
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( e.getMessage() ).build();
        }
    }


    @Override
    public Response deleteUrl( final String urlId )
    {
        try
        {
            kurjunManager.deleteUrl( Integer.parseInt( urlId ) );
            return Response.status( Response.Status.OK ).build();
        }
        catch ( ConfigurationException e )
        {
            LOG.error( "Error in updating system settings:" + e.getMessage() );
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( e.getMessage() ).build();
        }
    }


    @Override
    public Response getSignedMessage( final String signedMsg, final String id )
    {
        if ( kurjunManager.authorizeUser( Integer.parseInt( id ), new String( Base64.decodeBase64( signedMsg ) ) )
                == null )
        {
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).build();
        }
        else
        {
            return Response.status( Response.Status.OK ).build();
        }
    }


    @Override
    public Response addUrl( final String url, final String type )
    {
        try
        {
            kurjunManager.saveUrl( url, Integer.parseInt( type ) );

            return Response.status( Response.Status.OK ).build();
        }
        catch ( ConfigurationException e )
        {
            LOG.error( "Error in saving URL:" + e.getMessage() );
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( e.getMessage() ).build();
        }
    }


    public void setKurjunManager( final KurjunManager kurjunManager )
    {
        this.kurjunManager = kurjunManager;
    }
}
