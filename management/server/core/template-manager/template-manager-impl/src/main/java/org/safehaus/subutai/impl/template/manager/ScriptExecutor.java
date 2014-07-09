package org.safehaus.subutai.impl.template.manager;

import java.util.Arrays;
import java.util.HashSet;
import org.safehaus.subutai.api.commandrunner.Command;
import org.safehaus.subutai.api.commandrunner.CommandRunner;
import org.safehaus.subutai.api.commandrunner.RequestBuilder;
import org.safehaus.subutai.shared.protocol.Agent;

class ScriptExecutor {

    private final CommandRunner commandRunner;
    int defaultTimeout = 60;

    public ScriptExecutor(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    public void setDefaultTimeout(int defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    public boolean execute(Agent agent, ActionType actionType, String... args) {
        return execute(agent, actionType, defaultTimeout, args);
    }

    public boolean execute(Agent agent, ActionType actionType, int timeout, String... args) {
        if(agent == null || actionType == null) return false;

        RequestBuilder rb = new RequestBuilder(actionType.buildCommand(args));
        rb.withTimeout(defaultTimeout);
        Command cmd = commandRunner.createCommand(rb,
                new HashSet<>(Arrays.asList(agent)));

        commandRunner.runCommand(cmd);
        return cmd.hasSucceeded();
    }
}
