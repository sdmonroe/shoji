package org.fusuma.application;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;

import org.apache.log4j.Logger;
import org.fusuma.crypto.DiffieHellman;
import org.fusuma.shoji.globals.Constants;
import org.fusuma.to.message.BaseMessage;
import org.fusuma.to.message.DHKeyMaterial;

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
	static Logger logger = Logger.getLogger(KeyExchange.class);
	static {
		try {
			Class.forName("org.fusuma.to.DHKeyMaterial"); // be sure to load TO's into classloader
		}
		catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
	}
	private static final Vector<String> listening = new Vector<String>();
	/**
	 * The Endpoint represents the underlying node. By making calls on the Endpoint, it assures that the message will be delivered to a KeyExchange on whichever node the message is intended for.
	 */
	HashMap<Id, Object[]> keys = new HashMap<Id, Object[]>();
	HashMap<String, Id> privateChannelIds = new HashMap<String, Id>();

	/**
	 * @param node
	 * @param mode
	 * @throws ApplicationException
	 */
	KeyExchange(Node node) throws ApplicationException {
		this.node = node;
		// We are only going to use one instance of this application on each PastryNode
		this.channel = Constants.CHANNEL_DH_PUBLIC_KEY_EXCHANGE;
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
	public void dispatchPublicMaterial(NodeHandle nh, Message message) {
		dispatchPublicMaterial(nh, null, message);
	}

	/**
	 * Route's a message to id
	 * 
	 * @param id
	 * @param message
	 */
	public void dispatchPublicMaterial(Id id, Message message) {
		dispatchPublicMaterial(null, id, message);
	}

	/**
	 * Routes a message to id, or send the message directly to nh, depending on which is not null. Give precedent to nh.
	 * 
	 * @param nh
	 * @param id
	 * @param message
	 */
	private void dispatchPublicMaterial(NodeHandle nh, Id nid, Message message) {
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
			if (b.isSelfAddressed()) {
				logger.info(this + "dropping self-addressed message");
				return;
			}
			if (b instanceof DHKeyMaterial) {
				DHKeyMaterial keyMaterial = (DHKeyMaterial) b;
				Object[] local = getKeyMaterial(to);
				if (local == null) {
					local = new Object[Constants.KEY_MATERIAL_ARRAY_SIZE];
					// o[Constants.KEY_MATERIAL_PARTNER_PUBLIC] = null;
					local[Constants.KEY_MATERIAL_PRIVATE] = keyMaterial.getPrivateKey();
					local[Constants.KEY_MATERIAL_PUBLIC] = keyMaterial.getPublicKey();
					local[Constants.KEY_MATERIAL_LOCAL_KEY_AGREEMENT] = keyMaterial.getLocalKeyAgreement();
					updateKeyMap(to, local);
				}
				// logger.info(this + " sending to " + to);
				// logger.info(new Date() + " -- " + this + " dispatching DHKeyMaterial " + keyMaterial.extractPublicKey());
				DHKeyMaterial dhkm = keyMaterial.extractPublicKey();
				dhkm.setTimestamp(new Date());
				endpoint.route(nid, dhkm, nh);
				return;
			}
			else {
				endpoint.route(nid, b, nh);
				return;
			}
		}
		else {
			((BaseMessage) message).setTimestamp(new Date());
			endpoint.route(nid, message, nh);
			return;
		}
		// Message data = new BaseMessage(endpoint.getId(), id);
	}

	// /**
	// * Called to directly send a message to the nh
	// */
	// public void dispatch(NodeHandle nh, Message msg) {
	// logger.info(this + " sending direct to " + nh);
	// // Message data = new BaseMessage(endpoint.getId(), nh.getId());
	// endpoint.route(null, msg, nh);
	// }

	/**
	 * Called when we receive a message.
	 */
	@Override
	public void deliver(Id from, Message message) {
		if (message instanceof BaseMessage) {
			BaseMessage b = (BaseMessage) message;
			if (b.isSelfAddressed()) return;
			if (b instanceof DHKeyMaterial) {
				DHKeyMaterial partnerKeyMaterial = (DHKeyMaterial) b;
				// logger.info(new Date() + " -- " + this + " received DHKeyMaterial " + partnerKeyMaterial);
				Object[] local = new Object[0];

				switch (partnerKeyMaterial.getPhase()) {
					case Constants.KEY_PHASE_1:
						// o = new Object[2];
						// o[Constants.KEY_MATERIAL_PARTNER_PUBLIC] = partnerKeyMaterial;
						if (getKeyMaterial(partnerKeyMaterial.getFrom()) != null) return; // if a keys sync has already been initiated for the sender, do not accept sender's keys sync initiation proposal
						local = DiffieHellman.generatePhase2Material(partnerKeyMaterial.getPublicKey());
						// logger.info(this + " Shared Secret generated for receiver 1: " + new String(((byte[]) o[Constants.KEY_MATERIAL_SHARED_SECRET])));
						try {
							// DHPrivateKey privKey = ((DHPrivateKey) o[Constants.KEY_MATERIAL_PRIVATE]);
							// add the private channel

							BaseMessage bto = new BaseMessage(partnerKeyMaterial.getTo(), partnerKeyMaterial.getFrom(), "The channel is now open.");
							MessageExchange mex = joinChannel(partnerKeyMaterial, local);
							mex.dispatch(partnerKeyMaterial.getFrom(), bto);
							// send exponent across the public channel
							DHKeyMaterial dm = new DHKeyMaterial(partnerKeyMaterial.getTo(), partnerKeyMaterial.getFrom(), (DHPrivateKey) local[Constants.KEY_MATERIAL_PRIVATE], (DHPublicKey) local[Constants.KEY_MATERIAL_PUBLIC], (KeyAgreement) local[Constants.KEY_MATERIAL_LOCAL_KEY_AGREEMENT]);
							dm.setSharedSecret((byte[]) local[Constants.KEY_MATERIAL_SHARED_SECRET]);
							dm.setSecretHash((byte[]) local[Constants.KEY_MATERIAL_SECRET_HASH]);
							dm.setPhase(Constants.KEY_PHASE_2);
							// logger.info(this + " Shared Secret generated for sender: " + new String(dm.getSharedSecret()));
							dispatchPublicMaterial(partnerKeyMaterial.getFrom(), dm);
						}
						catch (ApplicationException e) {
							logger.error("Error creating keys exchange: " + e.getMessage(), e);
						}
						break;
					case Constants.KEY_PHASE_2:
						local = getKeyMaterial(partnerKeyMaterial.getFrom());
						DiffieHellman.generatePhase3Material(((KeyAgreement) local[Constants.KEY_MATERIAL_LOCAL_KEY_AGREEMENT]), partnerKeyMaterial.getPublicKey(), local);
						String they = new String(partnerKeyMaterial.getSharedSecret());
						String me = new String(((byte[]) local[Constants.KEY_MATERIAL_SHARED_SECRET]));
						// logger.info(new Date() + " -- " + "Me:" + me);
						// logger.info(new Date() + " -- " + "They: " + they);
						// logger.info(new Date() + " -- " + this + " Shared Secrets match 2: " + me.equals(they));
						// logger.info(new Date() + " -- " + this + " Key Material G match 2: " + partnerKeyMaterial.getPublicKey().getParams().getG().equals(((DHPrivateKey) local[Constants.KEY_MATERIAL_PRIVATE]).getParams().getG()));
						// logger.info(new Date() + " -- " + this + " Key Material P match 2: " + partnerKeyMaterial.getPublicKey().getParams().getP().equals(((DHPrivateKey) local[Constants.KEY_MATERIAL_PRIVATE]).getParams().getP()));
						try {
							BaseMessage bto = new BaseMessage(partnerKeyMaterial.getTo(), partnerKeyMaterial.getFrom(), "I'm here!! Anyone home?");
							MessageExchange mex = joinChannel(partnerKeyMaterial, local);
							mex.dispatch(partnerKeyMaterial.getFrom(), bto);
						}
						catch (ApplicationException e) {
							logger.error("Error creating keys exchange: " + e.getMessage(), e);
						}
						// o[Constants.KEY_MATERIAL_PARTNER_PUBLIC] = partnerKeyMaterial;
						// o[Constants.KEY_MATERIAL_PRIVATE] = DiffieHellman.generateSharedSecret((DHPrivateKey) o[Constants.KEY_MATERIAL_PRIVATE], (DHKeyMaterial) o[Constants.KEY_MATERIAL_PARTNER_PUBLIC]);
						// logger.info(this + " Shared Secret generated for receiver 2: " + new String(((byte[]) o[Constants.KEY_MATERIAL_SHARED_SECRET])));
						break;
				}

				updateKeyMap(partnerKeyMaterial.getFrom(), local);

				// switch (getMode()) {
				// case KeyExchange.MODE_PRIVATE:
				// String privateChannelId = parsePrivateChannelId();
				// Id mappedId = getId(privateChannelId);
				// if (mappedId == null || !mappedId.equals(((BaseMessage) message).getFrom())) break;
				// break;
				// case KeyExchange.MODE_PUBLIC:
				//
				// break;
				// }
			}
			else if (b instanceof BaseMessage) {

			}
		}
	}

	/**
	 * @param partnerKeyMaterial
	 * @param local
	 * @throws ApplicationException
	 */
	public MessageExchange joinChannel(DHKeyMaterial partnerKeyMaterial, Object[] local) throws ApplicationException {
		String pcid = new String((byte[]) local[Constants.KEY_MATERIAL_SECRET_HASH]);
		pcid = pcid.replace("\n", "");
		addPrivateChannel(pcid, partnerKeyMaterial.getFrom());
		// begin listening on the private channel
		return new MessageExchange(node, Constants.createURI(Constants.CHANNEL_PREFIX_PRIVATE + pcid));
	}

	// public URI parsePrivateChannelId() {
	// return Constants.createURI(getChannel().toString().substring(Constants.CHANNEL_PREFIX_PRIVATE.toString().length() + 1));
	// }

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

	public static Vector<String> getListening() {
		return listening;
	}

	public static void addListening(String channel) {
		getListening().add(channel);
	}

	public static boolean isListening(String channel) {
		return getListening().contains(channel);
	}
}
