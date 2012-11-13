package com.flowdock.plugins.jira;

import com.atlassian.query.Query;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.issue.IssueEventListener;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.flowdock.plugins.jira.config.FlowdockConfigurationManager;
import com.atlassian.crowd.embedded.api.User;

public class IssueChangeListener extends AbstractIssueEventListener implements
		IssueEventListener {
	private FlowdockEventRenderer eventRenderer;
	private FlowdockConfigurationManager flowdockConfigurationManager;
	private SearchService searchService;

	private static final String JQL_QUERY = "type='Bug'";
	
	public IssueChangeListener(EventPublisher publisher, FlowdockEventRenderer eventRenderer, FlowdockConfigurationManager manager, SearchService searchService) {
		// Automatically register myself as an event listener.
		publisher.register(this);
		
		this.eventRenderer = eventRenderer;
		this.flowdockConfigurationManager = manager;
		this.searchService = searchService;
	}
	
	@EventListener
	public void onIssueEvent(final IssueEvent event) {
		Thread t = new Thread() {
			public void run() {
				if (filterIssueEvent(event)) {
					System.out.println("Saatiin filtteroityy jee!");
					//String apiKey = this.flowdockConfigurationManager.getApiKeyForProject(event.getIssue().getProjectObject());
					//FlowdockConnection.sendApiMessage(this.eventRenderer.renderEvent(event), apiKey);
				} else {
					System.out.println("XXX filter returned false, not sending notification");
				}
			}
		};
		t.start();
	}

	/**
	 * Please do not load several instances of this change listener,
	 * because we don't want to send multiple messages.
	 */
	@Override
	public boolean isUnique() {
		return true;
	}

	/**
	 * Returns true if issue matches the JQL statement.
	 *
	 * Important: Needs to be run inside a separate thread, since this method
	 * will block until search index has been updated.
	 */
	private boolean filterIssueEvent(IssueEvent event) {
		User user = event.getUser();
		SearchService.ParseResult parsed = this.searchService.parseQuery(user, JQL_QUERY);
		if (!parsed.isValid()) {
			System.out.println("XXX is not valid!");
			for (String msg : parsed.getErrors().getErrorMessages()) {
				System.out.println(msg);
				return false;
			}
		}

		System.out.println("XXX is valid, got query");
		Query query = parsed.getQuery();

		try {
			long count = this.searchService.searchCount(user, query);
			System.out.println("XXX Search count = " + count);

			return true;
		} catch (SearchException se) {
			System.out.println("Got SearchException");
			System.out.println(se.getMessage());

			return false;
		}
	}
	
}
