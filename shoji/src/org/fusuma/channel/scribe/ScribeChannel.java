package org.fusuma.channel.scribe;

import java.net.URI;

import org.apache.log4j.Logger;
import org.fusuma.channel.AbstractChannel;
import org.fusuma.to.message.PublishContent;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;
import rice.p2p.scribe.Scribe;
import rice.p2p.scribe.ScribeImpl;

public class ScribeChannel extends AbstractChannel {
	static Logger logger = Logger.getLogger(ScribeChannel.class);
	// static {
	// try {
	// Class.forName("org.fusuma.to.BaseTo"); // be sure to load TO's into classloader
	// }
	// catch (ClassNotFoundException e) {
	// logger.error(e.getMessage(), e);
	// }
	// }
	protected boolean isListening = false;
	protected Scribe scribe = null;

	// public ScribeChannel(Node node, String uid) {
	// this(node, Constants.SCRIBE_DEFAULT_INSTANCE_ID, uid);
	// }

	public ScribeChannel(Node node, URI channel) {
		super(node, channel);
		scribe = new ScribeImpl(node, channel.toString());
		// this.channel = Constants.SCRIBE_DEFAULT_CHANNEL;
		// this.endpoint = node.buildEndpoint(this, uid);
		listen();
	}

	/**
	 * 
	 */
	// public void listen() {
	// if (!this.isListening) {
	// this.endpoint.register();
	// }
	// this.isListening = true;
	// }

	public Conversation joinConversation(URI topic) {
		Conversation c = new Conversation(this, topic);
		c.join();
		return c;
	}

	/**
	 * Part of the Application interface. Will receive PublishContent every so often.
	 */

	@Override
	public void deliver(Id id, Message message) {
		if (message instanceof PublishContent) {
			// sendMulticast();
			// sendAnycast();
			logger.info(getNode().getId() + " receiving publish content (" + message + ")");
		}
	}

	@Override
	public boolean forward(RouteMessage message) {
		return true;
	}

	public void update(NodeHandle handle, boolean joined) {

	}

	public Scribe getScribe() {
		return scribe;
	}

	public void setScribe(Scribe scribe) {
		this.scribe = scribe;
	}

}
