package org.safehaus.subutai.core.env.api;


import java.util.Set;
import java.util.UUID;

import org.safehaus.subutai.common.environment.Blueprint;
import org.safehaus.subutai.common.environment.Environment;
import org.safehaus.subutai.common.environment.EnvironmentModificationException;
import org.safehaus.subutai.common.environment.EnvironmentNotFoundException;
import org.safehaus.subutai.common.environment.Topology;
import org.safehaus.subutai.common.peer.ContainerHost;
import org.safehaus.subutai.core.env.api.exception.EnvironmentCreationException;
import org.safehaus.subutai.core.env.api.exception.EnvironmentDestructionException;
import org.safehaus.subutai.core.env.api.exception.EnvironmentManagerException;


/**
 * Environment Manager
 */
public interface EnvironmentManager
{

    /**
     * Returns all existing environments
     *
     * @return - set of {@code Environment}
     */
    public Set<Environment> getEnvironments();

    /**
     * Returns environment by id
     *
     * @param environmentId - environment id
     *
     * @return - {@code Environment}
     *
     * @throws EnvironmentNotFoundException - thrown if environment not found
     */
    public Environment findEnvironment( UUID environmentId ) throws EnvironmentNotFoundException;

    /**
     * Creates environment based on a passed topology
     *
     * @param name - environment name
     * @param topology - {@code Topology}
     * @param subnetCidr - subnet in CIDR-notation string, e.g. "192.168.0.1/16"
     * @param sshKey - optional ssh key content
     * @param async - indicates whether environment is created synchronously or asynchronously to the calling party
     *
     * @return - created environment
     *
     * @throws EnvironmentCreationException - thrown if error occurs during environment creation
     */
    public Environment createEnvironment( String name, Topology topology, String subnetCidr, String sshKey,
                                          boolean async ) throws EnvironmentCreationException;


    /**
     * Creates empty environment
     *
     * @param name - environment name
     * @param subnetCidr - subnet in CIDR-notation string, e.g. "192.168.0.1/16"
     * @param sshKey - ssh key content
     *
     * @return - id of created environment
     */
    public UUID createEmptyEnvironment( String name, String subnetCidr, String sshKey );

    /**
     * Destroys environment by id.
     *
     * @param environmentId - environment id
     * @param async - indicates whether environment is destroyed synchronously or asynchronously to the calling party
     * @param forceMetadataRemoval - if true, the call will remove environment metadata from database even if not all
     * containers were destroyed, otherwise an exception is thrown when first error occurs
     *
     * @throws EnvironmentDestructionException - thrown if error occurs during environment destruction
     * @throws EnvironmentNotFoundException - thrown if environment not found
     */
    public void destroyEnvironment( UUID environmentId, boolean async, boolean forceMetadataRemoval )
            throws EnvironmentDestructionException, EnvironmentNotFoundException;


    /**
     * Grows environment based on a passed topology
     *
     * @param environmentId - environment id
     * @param topology - {@code Topology}
     * @param async - indicates whether environment is grown synchronously or asynchronously to the calling party
     *
     * @return - set of newly created {@code ContainerHost} or empty set if operation is async
     *
     * @throws EnvironmentModificationException - thrown if error occurs during environment modification
     * @throws EnvironmentNotFoundException - thrown if environment not found
     */
    public Set<ContainerHost> growEnvironment( UUID environmentId, Topology topology, boolean async )
            throws EnvironmentModificationException, EnvironmentNotFoundException;

    /**
     * Assigns ssh key to environment and inserts it into authorized_keys file of all the containers within the
     * environment
     *
     * @param environmentId - environment id
     * @param sshKey - ssh key content
     * @param async - indicates whether ssh key is applied synchronously or asynchronously to the calling party
     *
     * @throws EnvironmentNotFoundException - thrown if environment not found
     * @throws EnvironmentModificationException - thrown if error occurs during key insertion
     */
    public void setSshKey( UUID environmentId, String sshKey, boolean async )
            throws EnvironmentNotFoundException, EnvironmentModificationException;


    /**
     * Destroys container. If this is the last container, the associated environment will be removed too
     *
     * @param containerHost - container to destroy
     * @param async - indicates whether container is destroyed synchronously or asynchronously to the calling party
     * @param forceMetadataRemoval - if true, the call will remove container metadata from database even if container
     * was not destroyed due to some error, otherwise an exception is thrown
     *
     * @throws EnvironmentModificationException - thrown if error occurs during environment modification
     * @throws EnvironmentNotFoundException - thrown if environment not found
     */
    public void destroyContainer( ContainerHost containerHost, boolean async, boolean forceMetadataRemoval )
            throws EnvironmentModificationException, EnvironmentNotFoundException;


    /**
     * Removes environment from database only. Used to cleanup environment records.
     *
     * @param environmentId - environment id
     *
     * @throws EnvironmentNotFoundException - thrown if environment not found
     */
    public void removeEnvironment( UUID environmentId ) throws EnvironmentNotFoundException;


    /**
     * Save environment blueprint
     *
     * @param blueprint - blueprint to save
     */
    public void saveBlueprint( Blueprint blueprint ) throws EnvironmentManagerException;


    /**
     * Remove blueprint from database
     *
     * @param blueprintId - blueprint id to remove
     */
    public void removeBlueprint( UUID blueprintId ) throws EnvironmentManagerException;


    /**
     * Get All blueprints
     *
     * @return - set of blueprints
     */
    public Set<Blueprint> getBlueprints() throws EnvironmentManagerException;


    /**
     * Get default domain name defaultDomainName: intra.lan
     *
     * @return - default domain name
     */
    public String getDefaultDomainName();
}
