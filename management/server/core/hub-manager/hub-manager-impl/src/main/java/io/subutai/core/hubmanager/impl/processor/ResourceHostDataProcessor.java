package io.subutai.core.hubmanager.impl.processor;


import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.time.DateUtils;

import com.google.common.collect.Lists;

import io.subutai.common.command.RequestBuilder;
import io.subutai.common.host.HostInfoModel;
import io.subutai.common.host.HostInterfaceModel;
import io.subutai.common.host.HostInterfaces;
import io.subutai.common.host.ResourceHostInfo;
import io.subutai.common.metric.QuotaAlertValue;
import io.subutai.common.metric.ResourceHostMetric;
import io.subutai.common.network.JournalCtlLevel;
import io.subutai.common.network.P2pLogs;
import io.subutai.common.peer.LocalPeer;
import io.subutai.common.peer.ResourceHost;
import io.subutai.core.hostregistry.api.HostListener;
import io.subutai.core.hubmanager.impl.HubManagerImpl;
import io.subutai.core.hubmanager.impl.http.HubRestClient;
import io.subutai.core.hubmanager.impl.http.RestResult;
import io.subutai.core.metric.api.Monitor;
import io.subutai.core.metric.api.pojo.P2Pinfo;
import io.subutai.hub.share.dto.HostInterfaceDto;
import io.subutai.hub.share.dto.P2PDto;
import io.subutai.hub.share.dto.host.ResourceHostMetricDto;
import io.subutai.hub.share.dto.SystemLogsDto;

import static java.lang.String.format;


public class ResourceHostDataProcessor implements Runnable, HostListener
{
    private final Logger log = LoggerFactory.getLogger( getClass() );

    private static HubManagerImpl hubManager;

    private static LocalPeer localPeer;

    private static HubRestClient restClient;

    private static Monitor monitor;

    private Date p2pLogsEndDate;

    private static Set<HostInterfaceDto> interfaces = new HashSet<>();


    public ResourceHostDataProcessor( HubManagerImpl hubManager, LocalPeer localPeer, Monitor monitor,
                                      HubRestClient restClient )
    {
        this.hubManager = hubManager;
        this.localPeer = localPeer;
        this.monitor = monitor;

        this.restClient = restClient;
    }


    public ResourceHostDataProcessor()
    {

    }


    @Override
    public void run()
    {
        try
        {
            process();
        }
        catch ( Exception e )
        {
            log.error( "Error to process resource host data: ", e );
        }
    }


    public void process() throws Exception
    {
        if ( hubManager.isRegistered() )
        {
            processConfigs();

            processP2PLogs();
        }
    }


    private void processConfigs()
    {
        for ( ResourceHostMetric rhMetric : monitor.getResourceHostMetrics().getResources() )
        {
            try
            {
                processConfigs( rhMetric );
            }
            catch ( Exception e )
            {
                log.error( "Error to process resource host configs: ", e );
            }
        }
    }


    private void processConfigs( ResourceHostMetric rhMetric ) throws Exception
    {
        log.info( "Getting resource host configs..." );

        ResourceHostMetricDto metricDto = getConfigs( rhMetric );

        metricDto.setInterfaces( interfaces );

        log.info( "Sending resource host configs to Hub..." );

        String path = format( "/rest/v1.2/peers/%s/resource-hosts/%s", localPeer.getId(), metricDto.getHostId() );

        RestResult<Object> restResult = restClient.post( path, metricDto );

        if ( restResult.isSuccess() )
        {
            log.info( "Resource host configs processed successfully" );
            interfaces.clear();
        }
    }


    private ResourceHostMetricDto getConfigs( ResourceHostMetric rhMetric )
    {
        ResourceHostMetricDto dto = new ResourceHostMetricDto();

        dto.setPeerId( localPeer.getId() );
        dto.setName( rhMetric.getHostInfo().getHostname() );
        dto.setHostId( rhMetric.getHostInfo().getId() );

        try
        {
            dto.setCpuModel( rhMetric.getCpuModel() );
        }
        catch ( Exception e )
        {
            log.error( "Error to get CPU model: {}", e.getMessage() );
        }

        try
        {
            dto.setMemory( rhMetric.getTotalRam() );
        }
        catch ( Exception e )
        {
            log.error( "Error to get total RAM: {}", e.getMessage() );
        }

        try
        {
            dto.setDisk( rhMetric.getTotalSpace() );
        }
        catch ( Exception e )
        {
            log.error( "Error to get total space: {}", e.getMessage() );
        }

        try
        {
            dto.setCpuCore( rhMetric.getCpuCore() );
        }
        catch ( Exception e )
        {
            log.error( "Error to get CPU cores: {}", e.getMessage() );
        }

        return dto;
    }


    private void processP2PLogs()
    {
        Date currentDate = new Date();

        Date startDate = p2pLogsEndDate != null ? p2pLogsEndDate : DateUtils.addMinutes( currentDate, -15 );

        p2pLogsEndDate = currentDate;

        for ( ResourceHost rh : localPeer.getResourceHosts() )
        {
            try
            {
                processP2PLogs( rh, startDate, currentDate );
            }
            catch ( Exception e )
            {
                log.error( "Error to process p2p logs: ", e );
            }
        }
    }


    private void processP2PLogs( ResourceHost rh, Date startDate, Date endDate ) throws Exception
    {
        log.info( "Getting p2p logs: {} - {}", startDate, endDate );

        P2pLogs p2pLogs = rh.getP2pLogs( JournalCtlLevel.ERROR, startDate, endDate );

        String p2pStatus = rh.execute( new RequestBuilder( "p2p status" ) ).getStdOut();

        List<P2PDto> p2pList = Lists.newArrayList();

        for ( final P2Pinfo info : monitor.getP2PStatus() )
        {
            P2PDto dto = new P2PDto();
            dto.setRhId( info.getRhId() );
            dto.setRhVersion( info.getRhVersion() );
            dto.setP2pVersion( info.getP2pVersion() );
            dto.setP2pStatus( info.getP2pStatus() );
            dto.setState( info.getState() );
            dto.setP2pErrorLogs( info.getP2pErrorLogs() );

            p2pList.add( dto );
        }

        log.info( "logs.size: {}", p2pLogs.getLogs().size() );

        if ( p2pLogs.isEmpty() && p2pStatus == null )
        {
            return;
        }

        SystemLogsDto logsDto = new SystemLogsDto();

        logsDto.setLogs( p2pLogs.getLogs() );
        logsDto.setStatus( p2pStatus );
        logsDto.setP2PInfo( p2pList );

        log.info( "Sending p2p logs and status to Hub..." );

        String path = format( "/rest/v1/peers/%s/resource-hosts/%s/system-logs", localPeer.getId(), rh.getId() );

        RestResult<Object> restResult = restClient.post( path, logsDto );

        if ( restResult.isSuccess() )
        {
            log.info( "Processing p2p logs completed successfully" );
        }
    }


    @Override
    public void onHeartbeat( final ResourceHostInfo resourceHostInfo, final Set<QuotaAlertValue> alerts )
    {
        // // TODO: 7/29/16 need optimize 
        if ( hubManager.isRegistered() )
        {
            HostInterfaces as = resourceHostInfo.getHostInterfaces();
            Set<HostInterfaceModel> test = as.getAll();

            for ( final HostInterfaceModel hostInterfaceModel : test )
            {
                HostInterfaceDto dto = new HostInterfaceDto();
                dto.setName( hostInterfaceModel.getName() );
                dto.setIp( hostInterfaceModel.getIp() );

                interfaces.add( dto );
            }
            processConfigs();
        }
    }
}
