package io.subutai.common.security.relation.model;


import io.subutai.common.security.relation.RelationLink;


public class RelationMeta
{
    private RelationLink source;

    private RelationLink target;

    // Key id to verify relation
    private String keyId;


    public RelationMeta()
    {
    }


    public RelationMeta( final RelationLink source, final RelationLink target,
                         final String keyId )
    {
        this.source = source;
        this.target = target;
        this.keyId = keyId;
    }


    public RelationLink getSource()
    {
        return source;
    }


    public void setSource( final RelationLink source )
    {
        this.source = source;
    }


    public RelationLink getTarget()
    {
        return target;
    }


    public void setTarget( final RelationLink target )
    {
        this.target = target;
    }


    public String getKeyId()
    {
        return keyId;
    }


    public void setKeyId( final String keyId )
    {
        this.keyId = keyId;
    }
}
