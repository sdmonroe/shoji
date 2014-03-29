package org.fusuma.application;

import rice.p2p.commonapi.Node;

public abstract class AbstractApplicationManager {
	protected Node node;

	public AbstractApplicationManager(Node node) {
		this.node = node;
	}

	public KeyExchange createKeyExchange(int mode) throws ApplicationException {
		KeyExchange kex = new KeyExchange(node, mode);
		kex.setApplicationManager(this);
		return kex;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

}
