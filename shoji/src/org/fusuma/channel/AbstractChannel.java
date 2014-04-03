package org.fusuma.channel;

import java.net.URI;

import org.apache.log4j.Logger;
import org.fusuma.shoji.globals.Constants;

import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Node;

public abstract class AbstractChannel implements Application {
	static Logger logger = Logger.getLogger(AbstractChannel.class);
	// static {
	// try {
	// Class.forName("org.fusuma.to.BaseTo"); // be sure to load TO's into classloader
	// }
	// catch (ClassNotFoundException e) {
	// logger.error(e.getMessage(), e);
	// }
	// }
	protected URI uid = null;
	protected AbstractChannelManager appManager;
	protected Endpoint endpoint;
	protected Node node;
	protected boolean isListening = false;

	public AbstractChannel(Node node, URI channel) {
		this.node = node;
		this.uid = channel;
		this.endpoint = node.buildEndpoint(this, channel.toString());
		// listen();
	}

	public AbstractChannel() {
		this.uid = Constants.CHANNEL_GENERAL;
	}

	/**
	 * 
	 */
	public void listen() {
		if (!this.isListening) {
			this.endpoint.register();
		}
		this.isListening = true;
	}

	public URI getUid() {
		return uid;
	}

	protected void setUid(URI channel) {
		this.uid = channel;
	}

	public AbstractChannelManager getApplicationManager() {
		return appManager;
	}

	public void setApplicationManager(AbstractChannelManager applicationManager) {
		this.appManager = applicationManager;
	}

	public Node getNode() {
		return node;
	}

	private void setNode(Node node) {
		this.node = node;
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	private void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

}
