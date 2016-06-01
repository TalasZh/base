package io.subutai.core.systemmanager.impl.model;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.subutai.core.systemmanager.api.model.SecuritySettings;


/**
 *
 */
@Entity
@Table( name = "security_settings" )
@Access( AccessType.FIELD )
public class SecuritySettingsEntity implements SecuritySettings
{
    @Id
    @Column( name = "peer_id" )
    private String peerId;

    @Column( name = "key_password" )
    private String keyPassword = "";

    @Column( name = "key_server_url" )
    private String keyServer = "";

    @Column( name = "key_trust_check" )
    private boolean keyTrustCheck = true;


    @Override
    public String getKeyServer()
    {
        return keyServer;
    }


    @Override
    public void setKeyServer( final String keyServer )
    {
        this.keyServer = keyServer;
    }


    @Override
    public String getPeerId()
    {
        return peerId;
    }


    @Override
    public void setPeerId( final String peerId )
    {
        this.peerId = peerId;
    }


    @Override
    public String getKeyPassword()
    {
        return keyPassword;
    }


    @Override
    public void setKeyPassword( final String keyPassword )
    {
        this.keyPassword = keyPassword;
    }


    @Override
    public boolean getKeyTrustCheck()
    {
        return keyTrustCheck;
    }


    @Override
    public void setKeyTrustCheck( final boolean keyTrustCheck )
    {
        this.keyTrustCheck = keyTrustCheck;
    }
}
