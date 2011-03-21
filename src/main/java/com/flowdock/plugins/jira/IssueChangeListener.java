package com.flowdock.plugins.jira;

import org.apache.log4j.Logger;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.issue.IssueEventListener;

public class IssueChangeListener extends AbstractIssueEventListener implements
		IssueEventListener {
	private static final Logger log = Logger.getLogger(IssueChangeListener.class);
	
	public IssueChangeListener(EventPublisher publisher) {
		// Automatically register myself as an event listener.
		publisher.register(this);
		System.out.println("XXX registering listener");
	}
	
	@EventListener
	public void onEvent(IssueEvent event) {
		System.out.println("XXX event handler: " + event.toString());
		log.info("XXX event handler for " + event.toString());
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
