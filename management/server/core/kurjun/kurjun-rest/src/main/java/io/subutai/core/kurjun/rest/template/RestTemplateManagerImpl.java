package io.subutai.core.kurjun.rest.template;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ai.subut.kurjun.metadata.common.subutai.DefaultTemplate;
import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.metadata.common.utils.IdValidators;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.repository.Repository;
import io.subutai.common.protocol.TemplateKurjun;
import io.subutai.core.kurjun.api.TemplateManager;
import io.subutai.core.kurjun.rest.RestManagerBase;


public class RestTemplateManagerImpl extends RestManagerBase implements RestTemplateManager
{

    private static final Logger LOGGER = LoggerFactory.getLogger( RestTemplateManagerImpl.class );

    private static final Gson GSON = new GsonBuilder().create();

    private final TemplateManager templateManager;


    public RestTemplateManagerImpl( TemplateManager templateManager )
    {
        this.templateManager = templateManager;
    }


    @Override
    public Response getTemplate( String repository, String id, String name, String version, String type,
                                 boolean isKurjunClient )
    {
        if ( repository == null )
        {
            repository = "public";
        }

        try
        {
            if ( id != null )
            {
                TemplateId tid = IdValidators.Template.validate( id );
                String md5bytes =  tid.getMd5();

                if ( md5bytes != null )
                {
                    TemplateKurjun template =
                            templateManager.getTemplateByMd5( repository, md5bytes, tid.getOwnerFprint(), isKurjunClient );
                    if ( template != null )
                    {
                        final String finalRepository = repository;

                        StreamingOutput sout = output -> {
                            final WritableByteChannel outChannel = Channels.newChannel( output );
                            templateManager.getTemplateData( finalRepository, md5bytes, tid.getOwnerFprint(),
                                    isKurjunClient, new Repository.PackageProgressListener()
                            {

                                @Override
                                public long getSize()
                                {
                                    return template.getSize();
                                }


                                @Override
                                public String downloadFileId()
                                {
                                    return tid.getMd5();
                                }


                                @Override
                                public void writeBytes( final ByteBuffer byteBuffer )
                                {
                                    try
                                    {
                                        while ( byteBuffer.hasRemaining() )
                                        {
                                            outChannel.write( byteBuffer );
                                        }
                                    }
                                    catch ( IOException e )
                                    {
                                        LOGGER.error( "Error writing to channel" );
                                    }
                                }
                            } );
                        };

                        Response.ResponseBuilder responseBuilder = Response.ok( sout );
                        responseBuilder.header( "Content-Disposition", "attachment; filename=" + template.getName() );
                        responseBuilder.header( "Content-Length", template.getSize() );
                        responseBuilder.header( "Content-Type", "application/octet-stream" );
                        return responseBuilder.build();
                    }
                }
            }
            else if ( RestTemplateManager.RESPONSE_TYPE_ID.equals( type ) )
            {
                TemplateKurjun template = templateManager.getTemplateByName( repository, name, version, isKurjunClient );

                if ( template != null )
                {
                    return Response.ok( template.getId() ).build();
                }
            }
        }
        catch ( IllegalArgumentException ex )
        {
            LOGGER.error( "", ex );
            return badRequest( ex.getMessage() );
        }
        catch ( IOException ex )
        {
            String msg = "Failed to get template info";
            LOGGER.error( msg, ex );
            return Response.serverError().entity( msg ).build();
        }
        return packageNotFoundResponse();
    }


    @Override
    public Response getTemplateInfo( String repository, String id, String name, String version, String type,
                                     boolean isKurjunClient )
    {
        if ( repository == null )
        {
            repository = "public";
        }
        if ( type == null )
        {
            type = "all";
        }
        try
        {
            if ( id != null )
            {
                TemplateId tid = IdValidators.Template.validate( id );

                if ( tid.getMd5() != null )
                {
                    TemplateKurjun template =
                            templateManager.getTemplateByMd5( repository, tid.getMd5(), tid.getOwnerFprint(), isKurjunClient );
                    if ( template != null )
                    {
                        return Response.ok( GSON.toJson( convertToDefaultTemplate( template ) ) ).build();
                    }
                }
            }

            TemplateKurjun template = templateManager.getTemplateByName( repository, name, version, isKurjunClient );

            if ( template != null )
            {
                switch ( type )
                {
                    case "json":
                        return Response.ok( GSON.toJson( convertToDefaultTemplate( template ).getId() ) ).build();
                    case "text":
                        return Response.ok( convertToDefaultTemplate( template ).getId() ).build();
                    default:
                        return Response.ok( GSON.toJson( convertToDefaultTemplate( template ) ) ).build();
                }
            }
        }
        catch ( IllegalArgumentException ex )
        {
            LOGGER.error( "", ex );
            return badRequest( ex.getMessage() );
        }
        catch ( IOException ex )
        {
            String msg = "Failed to get template info";
            LOGGER.error( msg, ex );
            return Response.serverError().entity( msg ).build();
        }
        return packageNotFoundResponse();
    }


    @Override
    public Response getTemplateList( String repository, boolean isKurjunClient )
    {
        if ( repository == null )
        {
            repository = "public";
        }

        try
        {
            List<TemplateKurjun> list = templateManager.list( repository, isKurjunClient );
            if ( list != null )
            {
                List<DefaultTemplate> deflist =
                        list.stream().map( this::convertToDefaultTemplate ).collect( Collectors.toList() );
                return Response.ok( GSON.toJson( deflist ) ).build();
            }
        }
        catch ( IllegalArgumentException ex )
        {
            LOGGER.error( "", ex );
            return badRequest( ex.getMessage() );
        }
        catch ( IOException ex )
        {
            String msg = "Failed to get template list info";
            LOGGER.error( msg, ex );
            return Response.serverError().entity( msg ).build();
        }
        return Response.ok( "No templates" ).build();
    }


    @Override
    public Response uploadTemplate( String repository, Attachment attachment )
    {
        if ( repository == null )
        {
            repository = "public";
        }

        File temp = null;
        try
        {
            temp = Files.createTempFile( null, null ).toFile();
            attachment.transferTo( temp );

            try ( InputStream is = new FileInputStream( temp ) )
            {
                String tid = templateManager.upload( repository, is );
                if ( tid != null )
                {
                    return Response.ok( tid ).build();
                }
                else
                {
                    return Response.serverError().entity( "Failed to put template" ).build();
                }
            }
        }
        catch ( IllegalArgumentException ex )
        {
            LOGGER.error( "", ex );
            return badRequest( ex.getMessage() );
        }
        catch ( IOException ex )
        {
            String msg = "Failed to put template";
            LOGGER.error( msg, ex );
            return Response.serverError().entity( msg ).build();
        }
        finally
        {
            FileUtils.deleteQuietly( temp );
        }
    }


    @Override
    public Response deleteTemplate( String repository, String id )
    {
        try
        {
            TemplateId tid = IdValidators.Template.validate( id );

            if ( tid.getMd5() != null )
            {
                try
                {
                    boolean deleted = templateManager.delete( repository, tid.getOwnerFprint(), tid.getMd5() );
                    if ( deleted )
                    {
                        return Response.ok( "Template deleted" ).build();
                    }
                    else
                    {
                        return packageNotFoundResponse();
                    }
                }
                catch ( IOException ex )
                {
                    String err = "Failed to delete template";
                    LOGGER.error( err, ex );
                    return Response.serverError().entity( err ).build();
                }
            }
            return badRequest( "Invalid md5 checksum" );
        }
        catch ( IllegalArgumentException ex )
        {
            LOGGER.error( "", ex );
            return badRequest( ex.getMessage() );
        }
    }


    private DefaultTemplate convertToDefaultTemplate( TemplateKurjun template )
    {
        return convertToDefaultTemplate( template, true );
    }


    private DefaultTemplate convertToDefaultTemplate( TemplateKurjun template, boolean includeFileContents )
    {
        DefaultTemplate defaultTemplate = new DefaultTemplate();
        defaultTemplate.setOwnerFprint( template.getOwnerFprint() );
        defaultTemplate.setName( template.getName() );
        defaultTemplate.setVersion( template.getVersion() );
        defaultTemplate.setMd5Sum(  template.getMd5Sum() );
        defaultTemplate.setArchitecture( Architecture.getByValue( template.getArchitecture() ) );
        defaultTemplate.setParent( template.getParent() );
        defaultTemplate.setSize( template.getSize() );
        defaultTemplate.setPackage( template.getPackageName() );
        if ( includeFileContents )
        {
            defaultTemplate.setConfigContents( template.getConfigContents() );
            defaultTemplate.setPackagesContents( template.getPackagesContents() );
        }
        return defaultTemplate;
    }


    private String makeFilename( TemplateKurjun t )
    {
        return t.getName() + "_" + t.getVersion() + ".tar.gz";
    }


    @Override
    protected Logger getLogger()
    {
        return LOGGER;
    }
}
