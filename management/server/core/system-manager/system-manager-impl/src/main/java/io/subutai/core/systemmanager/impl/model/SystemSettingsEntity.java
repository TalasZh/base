package io.subutai.core.systemmanager.impl.model;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.subutai.core.systemmanager.api.model.SystemSettings;


/**
 *
 */
@Entity
@Table( name = "system_settings" )
@Access( AccessType.FIELD )
public class SystemSettingsEntity implements SystemSettings
{
    @Id
    @Column( name = "peer_id" )
    private String peerId;

    @Column( name = "peer_owner_id" )
    private String peerOwnerId;

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
    public String getPeerOwnerId()
    {
        return peerOwnerId;
    }


    @Override
    public void setPeerOwnerId( final String peerOwnerId )
    {
        this.peerOwnerId = peerOwnerId;
    }
}
