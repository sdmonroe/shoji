package org.fusuma.channel.downstream;

import java.security.SecureRandom;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.fusuma.channel.AbstractChannelManager;
import org.fusuma.crypto.Xor;
import org.fusuma.to.message.DHKeyMaterial;
import org.fusuma.to.message.SAMTPPost;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Node;

public class Client extends AbstractChannelManager {
	static Logger logger = Logger.getLogger(Client.class);

	/**
	 * The slot belonging to the owner of this message
	 */
	private int slot;

	private LinkedHashMap<Id, DHKeyMaterial> serverKeys = new LinkedHashMap<Id, DHKeyMaterial>();

	public Client(Node node) {
		super(node);
		// SAMTPPost post = new SAMTPPost(null, 0);
		// setMessage(post, "This is a test");
		// String serverKeyXor = XorServerKeys(post.getPad().sumLengths());
		// post.setServerKeyXor(serverKeyXor);
	}

	public String XorServerKeys(int length) {
		String key = null;
		for (DHKeyMaterial km : getServerKeys().values()) {
			try {
				SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
				sr.setSeed(km.getSharedSecret());
				byte[] b = new byte[length];
				sr.nextBytes(b);
				if (key == null) key = new String(b);
				else {
					String str = new String(b);
					Xor xor = new Xor(str);
					key = xor.otp(key);
				}
			}
			catch (Exception e) {
				logger.error(e);
			}
		}
		return key;
	}

	private void addServerKey(Id id, DHKeyMaterial key) {
		getServerKeys().put(id, key);
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	/**
	 * Writes the String 's' to the slot belonging to the owner of this envelop.
	 * 
	 * @param s
	 */
	public void setMessage(SAMTPPost samtp, String msg) {
		samtp.getPad().set(getSlot(), msg);
	}

	public void appendMessage(SAMTPPost samtp, String msg) {
		String s = samtp.getPad().get(getSlot());
		s += msg;
		samtp.getPad().set(getSlot(), s);
	}

	public LinkedHashMap<Id, DHKeyMaterial> getServerKeys() {
		return serverKeys;
	}

	public void setServerKeys(LinkedHashMap<Id, DHKeyMaterial> keys) {
		this.serverKeys = keys;
	}

}
