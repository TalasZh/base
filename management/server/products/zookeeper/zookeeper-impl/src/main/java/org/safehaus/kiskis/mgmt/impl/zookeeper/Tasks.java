/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.safehaus.kiskis.mgmt.impl.zookeeper;

import java.util.Set;
import org.safehaus.kiskis.mgmt.shared.protocol.Agent;
import org.safehaus.kiskis.mgmt.api.taskrunner.Task;

/**
 *
 * @author dilshat
 */
public class Tasks {

    public static Task getInstallTask(Set<Agent> agents) {
        Task task = new Task();
        task.setData(TaskType.INSTALL);
        for (Agent agent : agents) {
            task.addRequest(Commands.getInstallCommand(), agent);
        }
        return task;
    }

    public static Task getStartTask(Agent agent) {
        Task task = new Task();
        task.setData(TaskType.START);
        task.addRequest(Commands.getStartCommand(), agent);
        return task;
    }

    public static Task getStopTask(Agent agent) {
        Task task = new Task();
        task.setData(TaskType.STOP);
        task.addRequest(Commands.getStopCommand(), agent);
        return task;
    }

    public static Task getStatusTask(Agent agent) {
        Task task = new Task();
        task.setData(TaskType.STATUS);
        task.addRequest(Commands.getStatusCommand(), agent);
        return task;
    }

    public static Task getCatCfgFileTask(Agent agent) {
        Task task = new Task();
        task.setData(TaskType.CAT_CFG_FILE);
        task.addRequest(Commands.getCatCfgFileCommand(), agent);
        return task;
    }
}
