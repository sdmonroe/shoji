package org.fusuma.to.message;

import rice.p2p.commonapi.Message;

public class PublishContent implements Message {
	public int getPriority() {
		return MAX_PRIORITY;
	}
}
