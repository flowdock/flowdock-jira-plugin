package com.flowdock.plugins.jira;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.issue.IssueEventListener;

public class IssueChangeListener extends AbstractIssueEventListener implements
		IssueEventListener {
	private FlowdockEventRenderer eventRenderer;
	
	public IssueChangeListener(EventPublisher publisher, FlowdockEventRenderer eventRenderer) {
		// Automatically register myself as an event listener.
		publisher.register(this);
		
		this.eventRenderer = eventRenderer;
	}
	
	@EventListener
	public void onIssueEvent(IssueEvent event) {
		FlowdockConnection.sendApiMessage(this.eventRenderer.renderEvent(event), "1234");
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
