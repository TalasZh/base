package org.safehaus.subutai.plugin.hadoop.impl.operation;

import org.safehaus.subutai.api.dbmanager.DBException;
import org.safehaus.subutai.api.lxcmanager.LxcDestroyException;
import org.safehaus.subutai.plugin.hadoop.api.HadoopClusterConfig;
import org.safehaus.subutai.plugin.hadoop.impl.HadoopImpl;
import org.safehaus.subutai.shared.operation.ProductOperation;
import org.safehaus.subutai.shared.protocol.Agent;

import java.util.UUID;

/**
 * Created by daralbaev on 08.04.14.
 */
public class Deletion {

	private HadoopImpl parent;

	public Deletion(HadoopImpl parent) {
		this.parent = parent;
	}

	public UUID execute(final String clusterName) {
		final ProductOperation po
				= HadoopImpl.getTracker().createProductOperation(HadoopClusterConfig.PRODUCT_KEY,
				String.format("Destroying cluster %s", clusterName));

		HadoopImpl.getExecutor().execute(new Runnable() {

			public void run() {
				HadoopClusterConfig hadoopClusterConfig;
				try {
					hadoopClusterConfig = parent.getPluginDAO().getInfo(HadoopClusterConfig.PRODUCT_KEY, clusterName, HadoopClusterConfig.class);
				} catch (DBException e) {
					po.addLogFailed(String.format("Cluster with name %s does not exist\nOperation aborted", clusterName));
					return;
				}

				po.addLog("Updating db...");
				try {
					parent.getPluginDAO().deleteInfo(HadoopClusterConfig.PRODUCT_KEY, hadoopClusterConfig.getClusterName());
					po.addLogDone("Cluster info deleted from DB\nDone");
				} catch (DBException e) {
					po.addLogFailed("Error while deleting cluster info from DB. Check logs.\nFailed");
				}

				po.addLog("Destroying lxc containers...");

				try {
					for (Agent agent : hadoopClusterConfig.getAllNodes()) {
						HadoopImpl.getContainerManager().cloneDestroy(agent.getParentHostName(), agent.getHostname());
					}
					po.addLog("Lxc containers successfully destroyed");
				} catch (LxcDestroyException ex) {
					po.addLog(String.format("%s, skipping...", ex.getMessage()));
				}
			}
		});

		return po.getId();
	}
}
