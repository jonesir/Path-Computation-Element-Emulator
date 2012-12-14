package com.pcee.client.event.impl;

import com.pcee.client.connectionsource.Source;
import com.pcee.client.event.Event;
import com.pcee.client.event.eventhandler.EventHandler;
import com.pcee.client.resv.ResvElement;

public class ReserveConnection extends Event {

	private ResvElement element;

	private Source source;

	public ReserveConnection(ResvElement element, Source source) {
		this.element = element;
		this.setTime(element.getStartTime());
		priority = 1;
		this.source = source;
	}

	@Override
	public void execute() {
		// for hierarchical pce supporting it resource with single path scenario
		boolean temp = element.reserveConnection();

		if (temp == true) {
			EventHandler.addEvent(new ReleaseConnection(element, source));
			source.connectionReserved();
		} else {
			source.connectionBlocked();
		}
		source.nextRequest(true);// insert new path computation request

	}

}
