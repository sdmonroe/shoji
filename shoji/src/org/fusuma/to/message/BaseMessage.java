package org.fusuma.to.message;

import java.net.URI;
import java.util.Date;

import org.apache.log4j.Logger;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;

/**
 * An example message.
 * 
 * @author Jeff Hoye
 */
public class BaseMessage implements rice.p2p.commonapi.Message {
	protected static transient Logger logger = Logger.getLogger(BaseMessage.class);
	/**
	 * Where the Message came from.
	 */
	Id from;
	/**
	 * Where the Message is going.
	 */
	Id to;

	Date timestamp;

	URI channel;

	Object data;

	/**
	 * Constructor.
	 */
	public BaseMessage(Id from, Id to) {
		this(from, to, null);
	}

	public BaseMessage(Id from, Id to, Object data) {
		this.from = from;
		this.to = to;
		this.data = data;
	}

	public boolean isSelfAddressed() {
		if (getTo() == null || getFrom() == null) return false;
		return getTo().equals(getFrom());
	}

	public String toString() {
		String s = "\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< MESSAGE >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>";
		return s + "\nFrom: " + this.from + ((this.to != null) ? "\nTo: " + this.to : "") + "\nTimestamp: " + this.timestamp + "\nChannel: " + this.channel + "\nData:\n\n" + this.data + "\n\n";
	}

	/**
	 * Use low priority to prevent interference with overlay maintenance traffic.
	 */
	public int getPriority() {
		return Message.LOW_PRIORITY;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
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

	public URI getChannel() {
		return channel;
	}

	public void setChannel(URI channel) {
		this.channel = channel;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp() {
		setTimestamp(new Date());
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
