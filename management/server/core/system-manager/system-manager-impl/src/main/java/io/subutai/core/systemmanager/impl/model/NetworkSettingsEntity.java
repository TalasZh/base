package io.subutai.core.systemmanager.impl.model;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.subutai.core.systemmanager.api.model.NetworkSettings;


/**
 *
 */
@Entity
@Table( name = "network_settings" )
@Access( AccessType.FIELD )
public class NetworkSettingsEntity implements NetworkSettings
{
    @Id
    @Column( name = "peer_id" )
    private String peerId;


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
}
