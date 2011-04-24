package com.flowdock.plugins.jira;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.issue.IssueEventListener;
import com.flowdock.plugins.jira.config.FlowdockConfigurationManager;

public class IssueChangeListener extends AbstractIssueEventListener implements
		IssueEventListener {
	private FlowdockEventRenderer eventRenderer;
	private FlowdockConfigurationManager flowdockConfigurationManager;
	
	public IssueChangeListener(EventPublisher publisher, FlowdockEventRenderer eventRenderer, FlowdockConfigurationManager manager) {
		// Automatically register myself as an event listener.
		publisher.register(this);
		
		this.eventRenderer = eventRenderer;
		this.flowdockConfigurationManager = manager;
	}
	
	@EventListener
	public void onIssueEvent(IssueEvent event) {
		String apiKey = this.flowdockConfigurationManager.getApiKeyForProject(event.getIssue().getProjectObject());
		FlowdockConnection.sendApiMessage(this.eventRenderer.renderEvent(event), apiKey);
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
