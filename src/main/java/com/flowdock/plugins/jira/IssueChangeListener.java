package com.flowdock.plugins.jira;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.issue.IssueEventListener;
import com.flowdock.plugins.jira.config.FlowdockConfigurationManager;
import org.springframework.beans.factory.DisposableBean;

public class IssueChangeListener extends AbstractIssueEventListener implements
		IssueEventListener, DisposableBean {
	private FlowdockEventRenderer eventRenderer;
	private FlowdockConfigurationManager flowdockConfigurationManager;
	private EventPublisher publisher;
	
	public IssueChangeListener(EventPublisher publisher, FlowdockEventRenderer eventRenderer, FlowdockConfigurationManager manager) {
		// Automatically register myself as an event listener.
		publisher.register(this);

		this.publisher = publisher;
		this.eventRenderer = eventRenderer;
		this.flowdockConfigurationManager = manager;
	}
	
	@EventListener
	public void onIssueEvent(IssueEvent event) {
		String apiKeys = this.flowdockConfigurationManager.getApiKeyForProject(event.getIssue().getProjectObject());
		for(String apiKey: apiKeys.split(",")){
			FlowdockConnection.sendApiMessage(this.eventRenderer.renderEvent(event), apiKey);
		}
	}

	@Override
	public void destroy() throws Exception {
		this.publisher.unregister(this);
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
