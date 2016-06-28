package io.subutai.common.security.relation.model;


import java.util.Map;

import com.google.common.collect.Maps;

import io.subutai.common.security.objects.Ownership;


public class RelationInfoMeta
{
    //Permission, role
    private Ownership ownership = Ownership.ALL;

    private Map<String, String> relationTraits = Maps.newHashMap();


    public RelationInfoMeta()
    {
    }


    public RelationInfoMeta( final Ownership ownership, Map<String, String> traits )
    {
        this.ownership = ownership;
        this.relationTraits = traits;
    }


    public Map<String, String> getRelationTraits()
    {
        return relationTraits;
    }


    public void setRelationTraits( final Map<String, String> relationTraits )
    {
        this.relationTraits = relationTraits;
    }


    public Ownership getOwnership()
    {
        return ownership;
    }


    public void setOwnership( final Ownership ownership )
    {
        this.ownership = ownership;
    }
}
