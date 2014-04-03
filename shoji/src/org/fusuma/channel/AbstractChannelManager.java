package org.fusuma.channel;

import java.net.URI;

import org.fusuma.channel.scribe.ScribeChannel;

import rice.p2p.commonapi.Node;

public abstract class AbstractChannelManager {
	protected Node node;
	/**
	 * Both servers and clients must join the DH key channel
	 */
	protected DHChannel DHChannel;

	public AbstractChannelManager(Node node) {
		this.node = node;
	}

	public DHChannel joinDHChannel() throws ChannelException {
		DHChannel kex = new DHChannel(node);
		kex.setApplicationManager(this);
		this.DHChannel = kex;
		return kex;
	}

	public ScribeChannel joinScribeChannel(URI channel) throws ChannelException {
		ScribeChannel se = new ScribeChannel(node, channel);
		se.setApplicationManager(this);
		return se;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public DHChannel getDHChannel() {
		return DHChannel;
	}

	public void setDHChannel(DHChannel keyExchange) {
		this.DHChannel = keyExchange;
	}

}
