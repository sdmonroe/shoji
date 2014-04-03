package org.fusuma.application.scribe;

import java.net.URI;

import org.apache.log4j.Logger;
import org.fusuma.application.AbstractApplication;
import org.fusuma.shoji.globals.Constants;
import org.fusuma.to.message.PublishContent;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;
import rice.p2p.scribe.Scribe;
import rice.p2p.scribe.ScribeImpl;

public class ScribeExchange extends AbstractApplication {
	static Logger logger = Logger.getLogger(ScribeExchange.class);
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

	// public ScribeExchange(Node node, String channel) {
	// this(node, Constants.SCRIBE_DEFAULT_INSTANCE_ID, channel);
	// }

	public ScribeExchange(Node node, URI channel) {
		super(node, channel);
		scribe = new ScribeImpl(node, Constants.SCRIBE_DEFAULT_CHANNEL);
		// this.channel = Constants.SCRIBE_DEFAULT_CHANNEL;
		// this.endpoint = node.buildEndpoint(this, channel);
		// listen();
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
