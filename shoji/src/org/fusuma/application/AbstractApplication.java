package org.fusuma.application;

import org.fusuma.shoji.globals.Constants;

import rice.p2p.commonapi.Application;

public abstract class AbstractApplication implements Application {
	protected String channel = null;
	protected AbstractApplicationManager appManager;

	public AbstractApplication() {
		this.channel = Constants.CHANNEL_GENERAL;
	}

	public void joinChannel(String channel) {
		setChannel(channel);
	}

	public String getChannel() {
		return channel;
	}

	protected void setChannel(String channel) {
		this.channel = channel;
	}

	public AbstractApplicationManager getApplicationManager() {
		return appManager;
	}

	void setApplicationManager(AbstractApplicationManager applicationManager) {
		this.appManager = applicationManager;
	}

}
