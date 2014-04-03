package org.fusuma.application;

import java.net.URI;
import java.util.Date;

import org.apache.log4j.Logger;
import org.fusuma.to.message.BaseMessage;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;

public class MessageExchange extends AbstractApplication {
	static Logger logger = Logger.getLogger(MessageExchange.class);

	public MessageExchange(Node node, URI channel) throws ApplicationException {
		this.node = node;
		this.channel = channel;

		// We are only going to use one instance of this application on each PastryNode
		this.endpoint = node.buildEndpoint(this, channel.toString());

		// listen for messages
		listen();
	}

	/**
	 * Sends a message directly to nh
	 * 
	 * @param nh
	 * @param message
	 */
	public void dispatch(NodeHandle nh, Message message) {
		dispatch(nh, null, message);
	}

	/**
	 * Route's a message to id
	 * 
	 * @param id
	 * @param message
	 */
	public void dispatch(Id id, Message message) {
		dispatch(null, id, message);
	}

	/**
	 * @param nh
	 * @param nid
	 * @param message
	 */
	private void dispatch(NodeHandle nh, Id nid, Message message) {
		Id to = null;
		if (nh != null) {
			to = nh.getId();
		}
		else if (nid != null) {
			to = nid;
		}
		else logger.info(this + " null receiver!");
		if (message instanceof BaseMessage) {
			BaseMessage b = (BaseMessage) message;
			b.setChannel(getChannel());
			logger.info(this + " sending to " + to);
			logger.info(new Date() + " -- " + this + " dispatching BaseMessage " + b);
			endpoint.route(nid, b, nh);
			// try {
			// StartupHold.environment.getTimeSource().sleep(10000);
			// }
			// catch (InterruptedException e) {
			// logger.error(e.getMessage(), e);
			// }
		}
	}

	@Override
	public void deliver(Id from, Message message) {
		logger.info(new Date() + " -- " + this + "message received: " + message);
	}

	@Override
	public boolean forward(RouteMessage arg0) {
		return true;
	}

	@Override
	public void update(NodeHandle arg0, boolean arg1) {
	}

	public String toString() {
		return "MessageExchange " + endpoint.getId();
	}

}
