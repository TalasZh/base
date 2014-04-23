/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.safehaus.kiskis.mgmt.api.commandrunner;

import java.util.Map;
import java.util.UUID;

/**
 *
 * @author dilshat
 */
public interface Command {

    public boolean hasCompleted();

    public boolean hasSucceeded();

    public CommandStatus getCommandStatus();

    public Map<UUID, AgentResult> getResults();

    public UUID getCommandUUID();

    public void setData(Object data);

    public Object getData();

    public String getAllErrors();

    public String getDescription();

}
