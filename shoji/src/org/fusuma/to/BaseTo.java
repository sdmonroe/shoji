package org.fusuma.to;

import org.apache.log4j.Logger;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;

/**
 * An example message.
 * 
 * @author Jeff Hoye
 */
public class BaseTo implements rice.p2p.commonapi.Message {
	protected static transient Logger logger = Logger.getLogger(BaseTo.class);
	/**
	 * Where the Message came from.
	 */
	Id from;
	/**
	 * Where the Message is going.
	 */
	Id to;

	String data;

	/**
	 * Constructor.
	 */
	public BaseTo(Id from, Id to) {
		this(from, to, null);
	}

	public BaseTo(Id from, Id to, String data) {
		this.from = from;
		this.to = to;
		this.data = data;
	}

	public boolean isSelfAddressed() {
		if (getTo() == null || getFrom() == null) return false;
		return getTo().equals(getFrom());
	}

	public String toString() {
		return "Plain message from " + this.from + " to " + this.to + "\n" + "Data: " + this.data;
	}

	/**
	 * Use low priority to prevent interference with overlay maintenance traffic.
	 */
	public int getPriority() {
		return Message.LOW_PRIORITY;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Id getFrom() {
		return from;
	}

	public void setFrom(Id from) {
		this.from = from;
	}

	public Id getTo() {
		return to;
	}

	public void setTo(Id to) {
		this.to = to;
	}
}
