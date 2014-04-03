package org.fusuma.to.message;

import org.fusuma.to.ScribeTopic;

import rice.p2p.commonapi.Id;
import rice.p2p.scribe.ScribeContent;

/**
 * @author Jeff Hoye
 */
public class ScribePost extends BaseMessage implements ScribeContent {

	/**
	 * Simple constructor. Typically, you would also like some interesting payload for your application.
	 * 
	 * @param from
	 *            Who sent the message.
	 * @param nextLength
	 *            the sequence number of this content.
	 */

	ScribeTopic topic;

	public ScribePost(Id from) {
		super(from, null, null);
		// System.out.println(this+".ctor");
	}

	/**
	 * A String representation of this object
	 */
	public String toString() {
		return super.toString() + "\nTopic: " + this.topic;
	}

	public ScribeTopic getTopic() {
		return topic;
	}

	public void setTopic(ScribeTopic topic) {
		this.topic = topic;
	}

}
