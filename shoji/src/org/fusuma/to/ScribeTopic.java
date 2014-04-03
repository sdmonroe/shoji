package org.fusuma.to;

import java.net.URI;

import rice.p2p.commonapi.IdFactory;
import rice.p2p.scribe.Topic;

public class ScribeTopic extends Topic {

	private URI uri;

	// public ScribeTopic(Id id) {
	// super(id);
	// }

	public ScribeTopic(IdFactory factory, URI uri) {
		super(factory, uri.toString());
		this.uri = uri;
	}

	public URI getUri() {
		return uri;
	}

	public void setName(URI uri) {
		this.uri = uri;
	}

}
