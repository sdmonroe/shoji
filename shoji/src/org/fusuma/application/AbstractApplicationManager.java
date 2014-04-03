package org.fusuma.application;

import java.net.URI;

import org.fusuma.application.scribe.ScribeExchange;

import rice.p2p.commonapi.Node;

public abstract class AbstractApplicationManager {
	protected Node node;

	public AbstractApplicationManager(Node node) {
		this.node = node;
	}

	public KeyExchange createKeyExchange() throws ApplicationException {
		KeyExchange kex = new KeyExchange(node);
		kex.setApplicationManager(this);
		return kex;
	}

	public ScribeExchange createScribeExchange(URI channel) throws ApplicationException {
		ScribeExchange se = new ScribeExchange(node, channel);
		se.setApplicationManager(this);
		return se;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

}
