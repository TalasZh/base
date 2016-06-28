package io.subutai.common.security.relation.model;


import java.io.Serializable;
import java.util.Map;

import io.subutai.common.security.relation.RelationLink;


public interface Relation extends Serializable
{
    long getId();

    RelationLink getSource();

    RelationLink getTarget();

    Map<String, String> getRelationTraits();

    int getOwnershipLevel();

    RelationStatus getRelationStatus();

    // TODO should be omitted so that only system can change relation status
    void setRelationStatus( final RelationStatus relationStatus );

    String getKeyId();
}
