package io.subutai.core.object.relation.impl.model;


import java.util.HashMap;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import io.subutai.common.security.objects.Ownership;
import io.subutai.common.security.relation.RelationLink;
import io.subutai.common.security.relation.model.Relation;
import io.subutai.common.security.relation.model.RelationStatus;


@Entity
@Table( name = "relation" )
@Access( AccessType.FIELD )
public class RelationImpl implements Relation
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "relation_id" )
    private long id;

    @Column( name = "source_link" )
    @ManyToOne( cascade = CascadeType.MERGE, fetch = FetchType.EAGER )
    private RelationLinkImpl source;

    @Column( name = "target_link" )
    @ManyToOne( cascade = CascadeType.MERGE, fetch = FetchType.EAGER )
    private RelationLinkImpl target;

    @Enumerated( EnumType.STRING )
    @Column( name = "status", nullable = false )
    private RelationStatus relationStatus;

    @Column( name = "ownership_level" )
    private int ownershipLevel = Ownership.ALL.getLevel();

    /**
     * Public key id to verify signed message
     */
    @Column( name = "signature_key_id" )
    private String keyId;

    @ElementCollection( fetch = FetchType.EAGER )
    @CollectionTable( name = "relation_traits" )
    @MapKeyColumn( name = "trait_key" )
    @Column( name = "trait_value" )
    private Map<String, String> relationTraits = new HashMap<String, String>(); // maps from attribute name to value


    public RelationImpl()
    {
    }


    public RelationImpl( final RelationLink source, final RelationLink target, Ownership ownership,
                         final Map<String, String> traits, final String keyId )
    {
        this.source = new RelationLinkImpl( source );
        this.target = new RelationLinkImpl( target );
        this.relationStatus = RelationStatus.STATED;
        this.keyId = keyId;
        this.ownershipLevel = ownership.getLevel();
        if ( traits != null )
        {
            this.relationTraits.putAll( traits );
        }
    }


    @Override
    public long getId()
    {
        return id;
    }


    @Override
    public RelationLinkImpl getSource()
    {
        return source;
    }


    @Override
    public RelationLinkImpl getTarget()
    {
        return target;
    }


    @Override
    public RelationStatus getRelationStatus()
    {
        return relationStatus;
    }


    @Override
    public String getKeyId()
    {
        return keyId;
    }


    @Override
    public Map<String, String> getRelationTraits()
    {
        return relationTraits;
    }


    @Override
    public int getOwnershipLevel()
    {
        return ownershipLevel;
    }


    public void setRelationTraits( final Map<String, String> relationTraits )
    {
        this.relationTraits = relationTraits;
    }


    public void setRelationStatus( final RelationStatus relationStatus )
    {
        this.relationStatus = relationStatus;
    }


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof RelationImpl ) )
        {
            return false;
        }

        final RelationImpl relation = ( RelationImpl ) o;

        if ( source != null ? !source.equals( relation.source ) : relation.source != null )
        {
            return false;
        }
        if ( target != null ? !target.equals( relation.target ) : relation.target != null )
        {
            return false;
        }
        return !( keyId != null ? !keyId.equals( relation.keyId ) : relation.keyId != null );
    }


    @Override
    public int hashCode()
    {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + ( target != null ? target.hashCode() : 0 );
        result = 31 * result + ( keyId != null ? keyId.hashCode() : 0 );
        return result;
    }


    @Override
    public String toString()
    {
        return "RelationImpl{" +
                "id=" + id +
                ", source=" + source +
                ", target=" + target +
                ", relationStatus=" + relationStatus +
                ", keyId='" + keyId + '\'' +
                '}';
    }
}
