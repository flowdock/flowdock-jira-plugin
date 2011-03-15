package com.flowdock.plugins.jira;

import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.issue.IssueEventListener;

public class IssueChangeListener extends AbstractIssueEventListener implements
		IssueEventListener {
	private static final Logger log = Logger.getLogger(IssueChangeListener.class);

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return super.getDescription();
	}

	@Override
	public boolean isInternal() {
		// TODO Auto-generated method stub
		return super.isInternal();
	}

	/**
	 * Please do not load several instances of this change listener,
	 * because we don't want to send multiple messages.
	 */
	@Override
	public boolean isUnique() {
		return true;
	}

	@Override
	public void issueAssigned(IssueEvent event) {
		// TODO Auto-generated method stub
		super.issueAssigned(event);
	}

	@Override
	public void issueClosed(IssueEvent event) {
		// TODO Auto-generated method stub
		super.issueClosed(event);
	}

	@Override
	public void issueCommentEdited(IssueEvent event) {
		// TODO Auto-generated method stub
		super.issueCommentEdited(event);
	}

	@Override
	public void issueCommented(IssueEvent event) {
		System.out.println("XXX issue commented: " + event.getComment().getBody());
		// TODO Auto-generated method stub
		super.issueCommented(event);
	}

	@Override
	public void issueCreated(IssueEvent event) {
		// TODO Auto-generated method stub
		super.issueCreated(event);
	}

	@Override
	public void issueDeleted(IssueEvent event) {
		// TODO Auto-generated method stub
		super.issueDeleted(event);
	}

	@Override
	public void issueGenericEvent(IssueEvent event) {
		// TODO Auto-generated method stub
		super.issueGenericEvent(event);
	}

	@Override
	public void issueMoved(IssueEvent event) {
		// TODO Auto-generated method stub
		super.issueMoved(event);
	}

	@Override
	public void issueReopened(IssueEvent event) {
		// TODO Auto-generated method stub
		super.issueReopened(event);
	}

	@Override
	public void issueResolved(IssueEvent event) {
		// TODO Auto-generated method stub
		super.issueResolved(event);
	}

	@Override
	public void issueStarted(IssueEvent event) {
		// TODO Auto-generated method stub
		super.issueStarted(event);
	}

	@Override
	public void issueStopped(IssueEvent event) {
		// TODO Auto-generated method stub
		super.issueStopped(event);
	}

	@Override
	public void issueUpdated(IssueEvent event) {
		// TODO Auto-generated method stub
		super.issueUpdated(event);
	}
	
}
