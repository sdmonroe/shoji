package org.fusuma.application;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.fusuma.crypto.DiffieHellman;
import org.fusuma.shoji.globals.Constants;
import org.fusuma.to.BaseTo;
import org.fusuma.to.DHKey;
import org.fusuma.to.DHPrivateKey;

import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;

/**
 * A very simple application.
 * 
 * @author Jeff Hoye
 */
public class KeyExchange extends AbstractApplication {
	Logger logger = Logger.getLogger(KeyExchange.class);
	/**
	 * The Endpoint represents the underlying node. By making calls on the Endpoint, it assures that the message will be delivered to a KeyExchange on whichever node the message is intended for.
	 */
	protected Endpoint endpoint;
	protected Node node;
	HashMap<Id, Object[]> keys = new HashMap<Id, Object[]>();
	HashMap<String, Id> privateChannelIds = new HashMap<String, Id>();
	int mode = -1;

	public final static int MODE_PUBLIC = 100;
	public final static int MODE_PRIVATE = 200;

	/**
	 * @param node
	 * @param channel
	 * @throws ApplicationException
	 */
	KeyExchange(Node node, String channel) throws ApplicationException {
		this.node = node;
		// We are only going to use one instance of this application on each PastryNode
		this.endpoint = node.buildEndpoint(this, channel);
		this.mode = KeyExchange.MODE_PRIVATE;

		// listen for messages
		listen();
	}

	/**
	 * @param node
	 * @param mode
	 * @throws ApplicationException
	 */
	KeyExchange(Node node, int mode) throws ApplicationException {
		this.node = node;
		// We are only going to use one instance of this application on each PastryNode
		this.mode = mode;
		joinChannel(node, mode);

		// listen for messages
		listen();
	}

	/**
	 * @param node
	 * @param mode
	 * @throws ApplicationException
	 */
	public void joinChannel(Node node, int mode) throws ApplicationException {
		switch (mode) {
			case KeyExchange.MODE_PUBLIC:
				this.channel = Constants.CHANNEL_PUBLIC_KEY_EXCHANGE;
				break;
			default:
				throw new ApplicationException("Invalid mode: " + mode);
		}
		this.endpoint = node.buildEndpoint(this, channel);
	}

	/**
	 * 
	 */
	public void listen() {
		this.endpoint.register();
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
	 * Routes a message to id, or send the message directly to nh, depending on which is not null. Give precedent to nh.
	 * 
	 * @param nh
	 * @param id
	 * @param message
	 */
	private void dispatch(NodeHandle nh, Id id, Message message) {
		if (nh != null) logger.info(this + " sending to " + nh.getId());
		else if (id != null) logger.info(this + " sending to " + id);
		else logger.info(this + " null receiver!");
		if (message instanceof BaseTo) {
			BaseTo b = (BaseTo) message;
			if (b.isSelfAddressed()) return;
			if (b instanceof DHPrivateKey) {
				DHPrivateKey privateKey = (DHPrivateKey) b;
				Object[] o = getKeyMaterial(id);
				if (o == null) {
					o = new Object[2];
					o[Constants.KEY_MATERIAL_PARTNER_PUBLIC] = null;
					o[Constants.KEY_MATERIAL_PRIVATE] = privateKey;
					updateKeyMap(id, o);
				}
				endpoint.route(id, privateKey.extractPublicKey(), nh);
				return;
			}
			else {
				endpoint.route(id, message, nh);
				return;
			}
		}
		else {
			endpoint.route(id, message, nh);
			return;
		}
		// Message data = new BaseTo(endpoint.getId(), id);
	}

	// /**
	// * Called to directly send a message to the nh
	// */
	// public void dispatch(NodeHandle nh, Message msg) {
	// logger.info(this + " sending direct to " + nh);
	// // Message data = new BaseTo(endpoint.getId(), nh.getId());
	// endpoint.route(null, msg, nh);
	// }

	/**
	 * Called when we receive a message.
	 */
	@Override
	public void deliver(Id id, Message message) {
		if (message instanceof BaseTo) {
			BaseTo b = (BaseTo) message;
			if (b.isSelfAddressed()) return;
			if (b instanceof DHKey) {
				DHKey partnerPublicKey = (DHKey) b;
				Object[] o = getKeyMaterial(id);
				switch (getMode()) {
					case KeyExchange.MODE_PRIVATE:
						String privateChannelId = parsePrivateChannelId();
						Id mappedId = getId(privateChannelId);
						if (mappedId == null || !mappedId.equals(((BaseTo) message).getFrom())) break;
						break;
					case KeyExchange.MODE_PUBLIC:
						if (o == null) {
							o = new Object[2];
							o[Constants.KEY_MATERIAL_PARTNER_PUBLIC] = partnerPublicKey;
							o[Constants.KEY_MATERIAL_PRIVATE] = DiffieHellman.generatePrivateKey(partnerPublicKey);
							logger.info(this + " Shared Secret generated for receiver: " + ((DHPrivateKey) o[Constants.KEY_MATERIAL_PRIVATE]).getSharedSecret());
						}
						else {
							o[Constants.KEY_MATERIAL_PARTNER_PUBLIC] = partnerPublicKey;
							o[Constants.KEY_MATERIAL_PRIVATE] = DiffieHellman.generateSharedSecret((DHPrivateKey) o[Constants.KEY_MATERIAL_PRIVATE], (DHKey) o[Constants.KEY_MATERIAL_PARTNER_PUBLIC]);
							logger.info(this + " Shared Secret generated for sender: " + ((DHPrivateKey) o[Constants.KEY_MATERIAL_PRIVATE]).getSharedSecret());
						}
						updateKeyMap(id, o);

						try {
							DHPrivateKey privKey = ((DHPrivateKey) o[Constants.KEY_MATERIAL_PRIVATE]);
							// add the private channel
							String pcid = new String(privKey.getSharedSecretHash());
							addPrivateChannel(pcid, ((BaseTo) message).getFrom());
							// begin listening on the private channel
							new KeyExchange(node, Constants.CHANNEL_PREFIX_PRIVATE + pcid);
							// send exponent across the public channel
							dispatch(null, partnerPublicKey.getFrom(), privKey.extractPublicKey());
						}
						catch (ApplicationException e) {
							logger.error("Error creating key exchange: " + e.getMessage(), e);
						}
						break;
				}
				logger.info(this + " received DHKey " + partnerPublicKey);
				logger.info(this + " generated DHKey " + o[Constants.KEY_MATERIAL_PRIVATE]);

			}
			else if (b instanceof BaseTo) {

			}
		}
	}

	public String parsePrivateChannelId() {
		return getChannel().substring(Constants.CHANNEL_PREFIX_PRIVATE.length() + 1);
	}

	/**
	 * Called when you hear about a new neighbor. Don't worry about this method for now.
	 */
	@Override
	public void update(NodeHandle handle, boolean joined) {
	}

	/**
	 * Called when a message travels along your path. Don't worry about this method for now.
	 */
	@Override
	public boolean forward(RouteMessage message) {
		return true;
	}

	public String toString() {
		return "KeyExchange " + endpoint.getId();
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

	public void updateKeyMap(Id id, Object[] keyMaterial) {
		getKeys().put(id, keyMaterial);
	}

	public Object[] getKeyMaterial(Id id) {
		return getKeys().get(id);
	}

	public HashMap<Id, Object[]> getKeys() {
		return keys;
	}

	public void setKeys(HashMap<Id, Object[]> keys) {
		this.keys = keys;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public Id getId(String privateChannelId) {
		return getPrivateChannelIds().get(privateChannelId);
	}

	public void addPrivateChannel(String privateChannelId, Id id) {
		getPrivateChannelIds().put(privateChannelId, id);
	}

	public HashMap<String, Id> getPrivateChannelIds() {
		return privateChannelIds;
	}

	public void setPrivateChannelIds(HashMap<String, Id> privateChannelIds) {
		this.privateChannelIds = privateChannelIds;
	}
}
