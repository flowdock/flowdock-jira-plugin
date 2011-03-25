package com.flowdock.plugins.jira;

import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.issue.IssueEventListener;

public class IssueChangeListener extends AbstractIssueEventListener implements
		IssueEventListener {
	private static final Logger log = Logger.getLogger(IssueChangeListener.class);
	private FlowdockEventRenderer eventRenderer;
	
	public IssueChangeListener(EventPublisher publisher, FlowdockEventRenderer eventRenderer) {
		// Automatically register myself as an event listener.
		publisher.register(this);
		
		this.eventRenderer = eventRenderer;
	}
	
	@EventListener
	public void onIssueEvent(IssueEvent event) {
		System.out.println("XXX event handler: " + event.toString());
		log.info("XXX event handler for " + event.toString());
		
		for (Map.Entry<String, String> entry : this.eventRenderer.renderEvent(event).entrySet()) {
			System.out.println("RESULT: " + entry.getKey() + " = " + entry.getValue());
		}
	}

	/**
	 * Please do not load several instances of this change listener,
	 * because we don't want to send multiple messages.
	 */
	@Override
	public boolean isUnique() {
		return true;
	}
	
}
