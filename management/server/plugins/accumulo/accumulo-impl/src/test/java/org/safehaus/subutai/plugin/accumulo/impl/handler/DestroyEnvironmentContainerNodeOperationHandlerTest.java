package org.safehaus.subutai.plugin.accumulo.impl.handler;


import org.junit.Ignore;
import org.junit.Test;
import org.safehaus.subutai.common.protocol.AbstractOperationHandler;
import org.safehaus.subutai.common.tracker.OperationState;
import org.safehaus.subutai.plugin.accumulo.api.AccumuloClusterConfig;
import org.safehaus.subutai.plugin.accumulo.api.NodeType;
import org.safehaus.subutai.plugin.accumulo.impl.AccumuloImpl;
import org.safehaus.subutai.plugin.accumulo.impl.handler.mock.AccumuloImplMock;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;


@Ignore
public class DestroyEnvironmentContainerNodeOperationHandlerTest
{

    @Test
    public void testWithoutCluster()
    {
        AbstractOperationHandler operationHandler =
                new DestroyNodeOperationHandler( new AccumuloImplMock(), "test-cluster", "test-node", NodeType.Tracer );

        operationHandler.run();

        assertTrue( operationHandler.getTrackerOperation().getLog().contains( "not exist" ) );
        assertEquals( operationHandler.getTrackerOperation().getState(), OperationState.FAILED );
    }


    @Test
    public void testAgentNotConnected()
    {
        AccumuloImpl accumuloImpl =
                new AccumuloImplMock().setClusterAccumuloClusterConfig( new AccumuloClusterConfig() );
        AbstractOperationHandler operationHandler =
                new CheckNodeOperationHandler( accumuloImpl, "test-cluster", "test-node" );
        operationHandler.run();

        assertTrue( operationHandler.getTrackerOperation().getLog().contains( "not connected" ) );
        assertEquals( operationHandler.getTrackerOperation().getState(), OperationState.FAILED );
    }
}