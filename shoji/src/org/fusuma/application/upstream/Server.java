package org.fusuma.application.upstream;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.fusuma.application.AbstractApplicationManager;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Node;

public class Server extends AbstractApplicationManager {
	static Logger logger = Logger.getLogger(Server.class);

	private LinkedHashMap<Id, PublicKey> serverKeys = new LinkedHashMap<Id, PublicKey>();
	private KeyPair keys;

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

	public void addServerKey(Id id, PublicKey key) {
		getServerKeys().put(id, key);
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

}
