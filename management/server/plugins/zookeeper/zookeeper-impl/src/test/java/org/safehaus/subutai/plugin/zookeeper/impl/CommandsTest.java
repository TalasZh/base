package org.safehaus.subutai.plugin.zookeeper.impl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.safehaus.subutai.common.command.Command;
import org.safehaus.subutai.plugin.common.mock.CommandRunnerMock;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class CommandsTest {

    private static Commands commands;
    @BeforeClass
    public static void setUp() {
        commands = new Commands( new CommandRunnerMock() );
    }

    @Test
    public void testInstallCommand() {
        Command command = Commands.getInstallCommand(null);

        assertNotNull(command);
        assertEquals("apt-get --force-yes --assume-yes install " + Commands.PACKAGE_NAME, command.getDescription());
    }

    @Test
    public void getStartCommand() {
        Command command = Commands.getStartCommand(null);

        assertNotNull( command );
        assertEquals( "service zookeeper start", command.getDescription() );
    }

    @Test
    public void getRestartCommand() {
        Command command = Commands.getRestartCommand(null);

        assertNotNull( command );
        assertEquals( "service zookeeper restart", command.getDescription() );
    }

    @Test
    public void getStatusCommand() {
        Command command = Commands.getStatusCommand(null);

        assertNotNull( command );
        assertEquals( "service zookeeper status", command.getDescription() );
    }

    @Test
    public void getStopCommand() {
        Command command = Commands.getStopCommand(null);

        assertNotNull( command );
        assertEquals( "service zookeeper stop", command.getDescription() );
    }

    @Test
    public void testUninstallCommand() {
        Command command = Commands.getUninstallCommand(null);

        assertNotNull(command);
        assertEquals("apt-get --force-yes --assume-yes purge " + Commands.PACKAGE_NAME, command.getDescription());
    }


    @Test
    public void testCheckCommand() {
        Command command = Commands.getCheckInstalledCommand(null);

        assertNotNull(command);
        assertEquals("dpkg -l | grep '^ii' | grep ksks", command.getDescription() );
    }
}