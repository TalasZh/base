package io.subutai.core.registration.api.service;


import java.util.Set;

import io.subutai.common.host.HostArchitecture;
import io.subutai.common.host.HostInterface;
import io.subutai.core.registration.api.RegistrationStatus;


public interface RequestedHost
{
    public String getId();

    public String getHostname();

    public Set<HostInterface> getNetHostInterfaces();

    public Set<ContainerInfo> getHostInfos();

    public HostArchitecture getArch();

    public RegistrationStatus getStatus();

    public String getPublicKey();

    public String getRestHook();

    public void setRestHook( String restHook );

    public String getSecret();

    public String getCert();
}
