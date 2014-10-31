package org.safehaus.subutai.core.peer.impl;


import java.util.List;
import java.util.UUID;

import org.safehaus.subutai.common.protocol.Template;
import org.safehaus.subutai.core.strategy.api.Criteria;


/**
 * Request for container creation at a remote peer
 */
public class CreateContainerRequest
{
    private final UUID creatorPeerId;
    private final UUID environmentId;
    private final List<Template> templates;
    private final int quantity;
    private final String strategyId;
    private final List<Criteria> criteria;
    private final UUID requestId;


    public CreateContainerRequest( final UUID creatorPeerId, final UUID environmentId, final List<Template> templates,
                                   final int quantity, final String strategyId, final List<Criteria> criteria )
    {
        this.creatorPeerId = creatorPeerId;
        this.environmentId = environmentId;
        this.templates = templates;
        this.quantity = quantity;
        this.strategyId = strategyId;
        this.criteria = criteria;
        this.requestId = UUID.randomUUID();
    }


    public UUID getRequestId()
    {
        return requestId;
    }


    public UUID getCreatorPeerId()
    {
        return creatorPeerId;
    }


    public UUID getEnvironmentId()
    {
        return environmentId;
    }


    public List<Template> getTemplates()
    {
        return templates;
    }


    public int getQuantity()
    {
        return quantity;
    }


    public String getStrategyId()
    {
        return strategyId;
    }


    public List<Criteria> getCriteria()
    {
        return criteria;
    }
}