package io.subutai.core.systemmanager.api.model;


/**
 *
 */
public interface SecuritySettings
{
    String getKeyServer();


    void setKeyServer( String keyServer );


    String getPeerId();


    void setPeerId( String peerId );


    String getKeyPassword();


    void setKeyPassword( String keyPassword );


    boolean getKeyTrustCheck();


    void setKeyTrustCheck( boolean keyTrustCheck );
}
