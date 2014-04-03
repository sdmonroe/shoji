package org.fusuma.application.scribe;

import org.apache.log4j.Logger;
import org.fusuma.application.AbstractApplication;
import org.fusuma.shoji.globals.Constants;
import org.fusuma.to.ScribeTopic;
import org.fusuma.to.message.PublishContent;
import org.fusuma.to.message.ScribeMessage;

import rice.p2p.commonapi.CancellableTask;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;
import rice.p2p.scribe.Scribe;
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
public class ScribeExchangeTest extends AbstractApplication implements ScribeClient {
	static Logger logger = Logger.getLogger(ScribeExchangeTest.class);
	static {
		try {
			Class.forName("org.fusuma.to.ScribeMessage"); // be sure to load TO's into classloader
		}
		catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * The message sequence number. Will be incremented after each send.
	 */
	int seqNum = 0;

	/**
	 * This task kicks off publishing and anycasting. We hold it around in case we ever want to cancel the publishTask.
	 */
	CancellableTask publishTask;

	/**
	 * My handle to a exchange impl.
	 */
	Scribe scribe;

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
	public ScribeExchangeTest(Node node) {
		this.node = node;
		this.endpoint = node.buildEndpoint(this, "myinstance");

		// construct Scribe
		scribe = new ScribeImpl(node, "myScribeInstance");

		// construct the topic
		topic = new ScribeTopic(new PastryIdFactory(node.getEnvironment()), Constants.SCRIBE_TOPIC_CIPHERTEXTS);
		logger.info("topic = " + topic);

		// now we can receive messages
		endpoint.register();
	}

	/**
	 * Subscribes to topic.
	 */
	public void subscribe() {
		scribe.subscribe(topic, this);
	}

	// /**
	// * Starts the publish task.
	// */
	// public void startPublishTask() {
	// publishTask = endpoint.scheduleMessage(new PublishContent(), 5000);
	// }

	/**
	 * Part of the Application interface. Will receive PublishContent every so often.
	 */
	@Override
	public void deliver(Id id, Message message) {
		if (message instanceof PublishContent) {
			// sendMulticast();
			// sendAnycast();
			logger.info(getNode().getId() + " receiving publish content (" + topic + "," + message + ")");
		}
	}

	/**
	 * Sends the multicast message.
	 */
	public void sendMulticast(ScribeMessage message) {
		logger.info("Node " + endpoint.getLocalNodeHandle() + " broadcasting " + seqNum);
		scribe.publish(topic, message);
		seqNum++;
	}

	/**
	 * Called whenever we receive a published message.
	 */
	@Override
	public void deliver(Topic topic, ScribeContent content) {
		if (content instanceof ScribeMessage) {
			ScribeMessage sm = (ScribeMessage) content;
			logger.info(getNode().getId() + " receiving message (" + topic + "," + sm + ")");
			if (sm.getFrom() == null) {
				new Exception("Stack Trace").printStackTrace();
			}
		}
	}

	/**
	 * Sends an anycast message.
	 */
	public void sendAnycast(ScribeMessage message) {
		logger.info("Node " + endpoint.getLocalNodeHandle() + " anycasting " + seqNum);
		// ScribeMessage myMessage = new ScribeMessage(endpoint.getId(), seqNum);
		scribe.anycast(topic, message);
		seqNum++;
	}

	/**
	 * Called when we receive an anycast. If we return false, it will be delivered elsewhere. Returning true stops the message here.
	 */
	public boolean anycast(Topic topic, ScribeContent content) {
		boolean returnValue = scribe.getEnvironment().getRandomSource().nextInt(3) == 0;
		// logger.info("conversations.anycast(" + topic + "," + content + "):" + returnValue);
		return returnValue;
	}

	public void childAdded(Topic topic, NodeHandle child) {
		// logger.info("conversations.childAdded("+topic+","+child+")");
	}

	public void childRemoved(Topic topic, NodeHandle child) {
		// logger.info("conversations.childRemoved("+topic+","+child+")");
	}

	public void subscribeFailed(Topic topic) {
		// logger.info("conversations.childFailed("+topic+")");
	}

	public boolean forward(RouteMessage message) {
		return true;
	}

	public void update(NodeHandle handle, boolean joined) {

	}

	// class PublishContent implements Message {
	// public int getPriority() {
	// return MAX_PRIORITY;
	// }
	// }

	/************ Some passthrough accessors for the exchange *************/
	public boolean isRoot() {
		return scribe.isRoot(topic);
	}

	public NodeHandle getParent() {
		// NOTE: Was just added to the Scribe interface. May need to cast exchange to a
		// ScribeImpl if using 1.4.1_01 or older.
		return ((ScribeImpl) scribe).getParent(topic);
		// return exchange.getParent(topic);
	}

	public NodeHandle[] getChildren() {
		return scribe.getChildren(topic);
	}

	public Scribe getScribe() {
		return scribe;
	}

	public void setScribe(Scribe scribe) {
		this.scribe = scribe;
	}

	public ScribeTopic getTopic() {
		return topic;
	}

	public void setTopic(ScribeTopic topic) {
		this.topic = topic;
	}

}
