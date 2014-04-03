package org.fusuma.channel.upstream;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.fusuma.channel.AbstractChannelManager;
import org.fusuma.channel.GenericChannel;
import org.fusuma.shoji.globals.Constants;
import org.fusuma.to.message.BaseMessage;
import org.fusuma.to.message.SAMTPPost;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Node;

public class Server extends AbstractChannelManager {
	static Logger logger = Logger.getLogger(Server.class);

	private LinkedHashMap<Id, PublicKey> serverKeys = new LinkedHashMap<Id, PublicKey>();
	private KeyPair keys;
	private List<SAMTPPost> clientPosts = new ArrayList<SAMTPPost>();
	boolean quorumReached = false;

	public Server(Node node) {
		super(node);
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keyGen.initialize(1024, random);
			this.keys = keyGen.generateKeyPair();
		}
		catch (Exception e) {
			logger.error(e);
		}
	}

	public void addServerKey(Id serverId, PublicKey key) {
		if (getServerKeys().size() == Constants.SERVER_QUORUM) { return; }
		getServerKeys().put(serverId, key);
		if (getServerKeys().size() == Constants.SERVER_QUORUM) {
			setQuorumReached(true);
		}
		Set<Id> keys = getDHChannel().getSharedKeys().keySet();
		for (Id clientId : (Id[]) keys.toArray(new Id[keys.size()])) {
			Object[] local = getDHChannel().getSharedKeys().get(clientId);
			GenericChannel privateChannel = (GenericChannel) local[Constants.KEY_MATERIAL_PRIVATE_CHANNEL];
			BaseMessage bm = new BaseMessage(getNode().getId(), clientId, "Ready to begin round.");
			// TODO sort the server keys by alphanumeric order of ids
			// give client the server keys
			privateChannel.dispatch(clientId, bm);
		}
	}

	public LinkedHashMap<Id, PublicKey> getServerKeys() {
		return serverKeys;
	}

	public void setServerKeys(LinkedHashMap<Id, PublicKey> serverKeys) {
		this.serverKeys = serverKeys;
	}

	public org.fusuma.to.message.PublicKey getPublicKey() {
		return new org.fusuma.to.message.PublicKey(getNode().getId(), getKeys().getPublic());
	}

	private KeyPair getKeys() {
		return keys;
	}

	private void setPrivateKey(KeyPair key) {
		this.keys = key;
	}

	public boolean isQuorumReached() {
		return quorumReached;
	}

	public void setQuorumReached(boolean quorumReached) {
		this.quorumReached = quorumReached;
	}

	public List<SAMTPPost> getClientPosts() {
		return clientPosts;
	}

	public void setClientPosts(List<SAMTPPost> clientPosts) {
		this.clientPosts = clientPosts;
	}

	public void addClientPost(SAMTPPost post) {
		getClientPosts().add(post);
	}

	/**
	 * Shuffles the order of the clientPost list
	 */
	public void randomizeClientPostOrder() {

	}

}
