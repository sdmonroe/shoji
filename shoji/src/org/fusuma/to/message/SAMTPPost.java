package org.fusuma.to.message;

import org.fusuma.crypto.Xor;
import org.fusuma.to.SAMTPPad;

/**
 * An SAMTP message. This class cannot be extended, so as to prevent additional information to be appended to the message, information which may deanonymize the sender.
 * 
 * @author smonroe
 * 
 */
public final class SAMTPPost extends BaseMessage {

	private int nextLength = 0;
	private transient String serverKeyXor;

	public SAMTPPost(SAMTPPad data, int nextLength) {
		super(null, null);
		this.data = data;
		this.nextLength = nextLength;
	}

	public void xorPad() {
		int onset = 0;
		int index = 0;
		for (String s : getPad()) {
			String key = getServerKeyXor().substring(onset);
			Xor xor = new Xor(key);
			s = xor.otp(s);
			getPad().set(index, s);
			onset += s.length();
			index++;
		}
	}

	public SAMTPPad getPad() {
		return (SAMTPPad) getData();
	}

	public int getNextLength() {
		return nextLength;
	}

	public void setNextLength(int nextLength) {
		this.nextLength = nextLength;
	}

	private String getServerKeyXor() {
		return serverKeyXor;
	}

	public void setServerKeyXor(String serverKeyXor) {
		this.serverKeyXor = serverKeyXor;
	}

}