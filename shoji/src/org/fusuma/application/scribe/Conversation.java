package org.fusuma.application.scribe;

import java.net.URI;
import java.security.PublicKey;
import java.util.Date;

import org.apache.log4j.Logger;
import org.fusuma.application.AbstractApplicationManager;
import org.fusuma.application.upstream.Server;
import org.fusuma.shoji.globals.Constants;
import org.fusuma.to.ScribeTopic;
import org.fusuma.to.message.BaseMessage;
import org.fusuma.to.message.ScribeMessage;

import rice.p2p.commonapi.CancellableTask;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.scribe.ScribeClient;
import rice.p2p.scribe.ScribeContent;
import rice.p2p.scribe.ScribeImpl;
import rice.p2p.scribe.Topic;
import rice.pastry.commonapi.PastryIdFactory;

/**
 * We implement the Application interface to receive regular timed messages (see lesson5). We implement the ScribeClient interface to receive exchange messages (called ScribeContent).
 * 
 * @author Jeff Hoye
 */
public class Conversation implements ScribeClient {
	static Logger logger = Logger.getLogger(Conversation.class);
	// static {
	// try {
	// Class.forName("org.fusuma.to.ScribeMessage"); // be sure to load TO's into classloader
	// }
	// catch (ClassNotFoundException e) {
	// logger.error(e.getMessage(), e);
	// }
	// }

	// /**
	// * The message sequence number. Will be incremented after each send.
	// */
	// int seqNum = 0;

	/**
	 * This task kicks off publishing and anycasting. We hold it around in case we ever want to cancel the publishTask.
	 */
	CancellableTask publishTask;

	/**
	 * My handle to a exchange impl.
	 */
	ScribeExchange exchange;

	/**
	 * The only topic this appl is subscribing to.
	 */
	ScribeTopic topic;

	/**
	 * The Endpoint represents the underlining node. By making calls on the Endpoint, it assures that the message will be delivered to a MyApp on whichever node the message is intended for.
	 */
	// public Endpoint endpoint;

	/**
	 * The constructor for this exchange client. It will construct the ScribeExchange.
	 * 
	 * @param node
	 *            the PastryNode
	 */
	Conversation(ScribeExchange exchange, URI scribeTopic) {
		// this.node = node;
		// this.endpoint = node.buildEndpoint(this, "myinstance");

		// construct Scribe
		// exchange = new ScribeImpl(node, "myScribeInstance");
		this.exchange = exchange;

		// construct the topic
		topic = new ScribeTopic(new PastryIdFactory(this.exchange.getNode().getEnvironment()), scribeTopic);
		logger.info("topic = " + topic);

		// now we can receive messages
		// endpoint.register();
	}

	/**
	 * Subscribes to topic.
	 */
	public void join() {
		exchange.getScribe().subscribe(topic, this);
	}

	// /**
	// * Starts the publish task.
	// */
	// public void startPublishTask() {
	// publishTask = endpoint.scheduleMessage(new PublishContent(), 5000);
	// }

	/**
	 * Sends an anycast message.
	 */
	public void sendAnycast(ScribeMessage message) {
		message.setTimestamp(new Date());
		logger.info("Node " + getExchange().getEndpoint().getLocalNodeHandle() + " anycasting ");
		// ScribeMessage myMessage = new ScribeMessage(endpoint.getId(), seqNum);
		exchange.getScribe().anycast(topic, message);
		// seqNum++;
	}

	/**
	 * Sends the multicast message.
	 */
	public void sendMulticast(ScribeMessage message) {
		message.setTimestamp(new Date());
		logger.info("Node " + getExchange().getEndpoint().getLocalNodeHandle() + " broadcasting ");
		exchange.getScribe().publish(topic, message);
		// seqNum++;
	}

	/**
	 * Called whenever we receive a published message.
	 */
	@Override
	public void deliver(Topic topic, ScribeContent content) {
		if (content instanceof BaseMessage) {
			BaseMessage b = (BaseMessage) content;
			if (b.isSelfAddressed()) {
				logger.info(this + "dropping self-addressed message");
				return;
			}
			if (getExchange().getNode().getId().equals(b.getFrom())) {
				logger.info(this + "dropping message from self");
				return;
			}
			if (content instanceof ScribeMessage) {
				ScribeMessage sm = (ScribeMessage) content;
				logger.info(getExchange().getNode().getId() + " receiving message (" + topic + "," + sm + ")");
				AbstractApplicationManager appManager = getExchange().getApplicationManager();
				if (appManager instanceof Server) {
					Server server = (Server) appManager;
					if (topic instanceof ScribeTopic) {
						ScribeTopic st = (ScribeTopic) topic;
						if (st.getUri().equals(Constants.SCRIBE_TOPIC_PUBLIC_KEYS)) {
							if (sm instanceof PublicKey) {
								server.addServerKey(sm.getFrom(), (PublicKey) sm);
							}
						}
					}
				}
				if (sm.getFrom() == null) {
					new Exception("Stack Trace").printStackTrace();
				}
			}
		}
	}

	/**
	 * Called when we receive an anycast. If we return false, it will be delivered elsewhere. Returning true stops the message here.
	 * 
	 * TODO need to return true or false
	 */
	@Override
	public boolean anycast(Topic topic, ScribeContent content) {
		boolean returnValue = exchange.getScribe().getEnvironment().getRandomSource().nextInt(3) == 0;
		// logger.info("conversations.anycast(" + topic + "," + content + "):" + returnValue);
		return false;
	}

	@Override
	public void childAdded(Topic topic, NodeHandle child) {
		// logger.info("conversations.childAdded("+topic+","+child+")");
	}

	@Override
	public void childRemoved(Topic topic, NodeHandle child) {
		// logger.info("conversations.childRemoved("+topic+","+child+")");
	}

	@Override
	public void subscribeFailed(Topic topic) {
		// logger.info("conversations.childFailed("+topic+")");
	}

	/************ Some passthrough accessors for the exchange *************/
	public boolean isRoot() {
		return exchange.getScribe().isRoot(topic);
	}

	public NodeHandle getParent() {
		// NOTE: Was just added to the Scribe interface. May need to cast exchange to a
		// ScribeImpl if using 1.4.1_01 or older.
		return ((ScribeImpl) exchange.getScribe()).getParent(topic);
		// return exchange.getParent(topic);
	}

	public NodeHandle[] getChildren() {
		return exchange.getScribe().getChildren(topic);
	}

	public ScribeExchange getExchange() {
		return exchange;
	}

	public void setExchange(ScribeExchange exchange) {
		this.exchange = exchange;
	}

	public ScribeTopic getTopic() {
		return topic;
	}

	public void setTopic(ScribeTopic topic) {
		this.topic = topic;
	}

}
