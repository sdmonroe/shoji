package org.fusuma.application;

import java.net.URI;

import org.apache.log4j.Logger;
import org.fusuma.shoji.globals.Constants;

import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Node;

public abstract class AbstractApplication implements Application {
	static Logger logger = Logger.getLogger(AbstractApplication.class);
	// static {
	// try {
	// Class.forName("org.fusuma.to.BaseTo"); // be sure to load TO's into classloader
	// }
	// catch (ClassNotFoundException e) {
	// logger.error(e.getMessage(), e);
	// }
	// }
	protected URI channel = null;
	protected AbstractApplicationManager appManager;
	protected Endpoint endpoint;
	protected Node node;
	protected boolean isListening = false;

	public AbstractApplication(Node node, URI channel) {
		this.node = node;
		this.channel = channel;
		this.endpoint = node.buildEndpoint(this, channel.toString());
		listen();
	}

	public AbstractApplication() {
		this.channel = Constants.CHANNEL_GENERAL;
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

	public URI getChannel() {
		return channel;
	}

	protected void setChannel(URI channel) {
		this.channel = channel;
	}

	public AbstractApplicationManager getApplicationManager() {
		return appManager;
	}

	public void setApplicationManager(AbstractApplicationManager applicationManager) {
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
