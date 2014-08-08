/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.safehaus.subutai.impl.lxcmanager;


import org.safehaus.subutai.api.commandrunner.Command;
import org.safehaus.subutai.api.commandrunner.CommandsSingleton;
import org.safehaus.subutai.api.commandrunner.RequestBuilder;
import org.safehaus.subutai.shared.protocol.Agent;
import org.safehaus.subutai.shared.protocol.Util;

import java.util.Set;


/**
 * Commands for lxc management
 */
public class Commands extends CommandsSingleton {

	public static Command getCloneCommand(Agent physicalAgent, String lxcHostName) {
		return createCommand(new RequestBuilder(
				String.format("/usr/bin/lxc-clone -o base-container -n %1$s && addhostlxc %1$s", lxcHostName))
				.withTimeout(360), Util.wrapAgentToSet(physicalAgent));
	}


	public static Command getCloneNStartCommand(Agent physicalAgent, String lxcHostName) {
		return createCommand(new RequestBuilder(String.format(
				"/usr/bin/lxc-clone -o base-container -n %1$s && addhostlxc %1$s && /usr/bin/lxc-start -n %1$s -d &",
				lxcHostName)).withTimeout(360), Util.wrapAgentToSet(physicalAgent));
	}


	public static Command getLxcListCommand(Set<Agent> agents) {
		return createCommand(new RequestBuilder("/usr/bin/lxc-ls -f").withTimeout(60), agents);
	}


	public static Command getLxcInfoCommand(Agent physicalAgent, String lxcHostName) {
		return createCommand(
				new RequestBuilder(String.format("/usr/bin/lxc-info -n %s", lxcHostName)).withTimeout(60),
				Util.wrapAgentToSet(physicalAgent));
	}


	public static Command getLxcStartCommand(Agent physicalAgent, String lxcHostName) {
		return createCommand(
				new RequestBuilder(String.format("/usr/bin/lxc-start -n %s -d &", lxcHostName)).withTimeout(180),
				Util.wrapAgentToSet(physicalAgent));
	}


	public static Command getLxcStopCommand(Agent physicalAgent, String lxcHostName) {
		return createCommand(
				new RequestBuilder(String.format("/usr/bin/lxc-stop -n %s &", lxcHostName)).withTimeout(180),
				Util.wrapAgentToSet(physicalAgent));
	}

	public static Command getLxcTerminateCommand(Agent physicalAgent, String lxcHostName) {
		return createCommand(
				new RequestBuilder(String.format("/usr/bin/lxc-stop -n -W %s &", lxcHostName)).withTimeout(180),
				Util.wrapAgentToSet(physicalAgent));
	}


	public static Command getLxcDestroyCommand(Agent physicalAgent, String lxcHostName) {
		return createCommand(new RequestBuilder(
				String.format("/usr/bin/lxc-destroy -n %1$s -f", lxcHostName))
				.withTimeout(180), Util.wrapAgentToSet(physicalAgent));
	}


	public static Command getMetricsCommand(Set<Agent> agents) {
		return createCommand(
				new RequestBuilder("free -m | grep buffers/cache ; df /lxc-data | grep /lxc-data ; uptime ; nproc")
						.withTimeout(30), agents);
	}
}
