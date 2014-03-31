package org.fusuma.application;

import org.fusuma.shoji.globals.Constants;

import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Node;

public abstract class AbstractApplication implements Application {
	protected String channel = null;
	protected AbstractApplicationManager appManager;
	protected Endpoint endpoint;
	protected Node node;
	protected static boolean isListening = false;

	public AbstractApplication() {
		this.channel = Constants.CHANNEL_GENERAL;
	}

	/**
	 * 
	 */
	public void listen() {
		if (!AbstractApplication.isListening) {
			this.endpoint.register();
		}
		AbstractApplication.isListening = true;
	}

	public void joinChannel(String channel) {
		setChannel(channel);
	}

	public String getChannel() {
		return channel;
	}

	protected void setChannel(String channel) {
		this.channel = channel;
	}

	public AbstractApplicationManager getApplicationManager() {
		return appManager;
	}

	void setApplicationManager(AbstractApplicationManager applicationManager) {
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
