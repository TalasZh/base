package org.safehaus.subutai.core.monitor.cli;


import java.util.Date;

import org.safehaus.subutai.core.monitor.api.Monitoring;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import com.google.common.base.Preconditions;


@Command( scope = "monitor", name = "all-metrics" )
public class AllMetricsCommand extends OsgiCommandSupport
{

    @Argument( index = 0, name = "hostname", required = true, multiValued = false )
    protected String hostname = null;

    private Monitoring monitoring;


    public void setMonitoring( Monitoring monitoring )
    {
        Preconditions.checkNotNull( monitoring, "Monitoring is null." );
        this.monitoring = monitoring;
    }


    protected Object doExecute()
    {

        Date endDate = new Date();
        Date startDate = DateUtils.addDays( endDate, -1 );

        //        Map<Metric, Map<Date, Double>> data = monitoring.getDataForAllMetrics( hostname, startDate, endDate );
        //
        //        System.out.println( "Data: " + data );

        return null;
    }
}
