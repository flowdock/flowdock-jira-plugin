package com.flowdock.plugins.jira;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;

public class FlowdockEventRenderer {
	private String baseUrl;
	
	public FlowdockEventRenderer() {
		// Magic trick to get the JIRA baseUrl.
		this.baseUrl = ManagerFactory.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
	}
	
	public Map<String, String> renderEvent(IssueEvent event) {
		HashMap<String, String> result = new HashMap<String, String>();
		
		// Comment: optional
		if (event.getComment() != null) {
			result.put("comment_body", event.getComment().getBody());
		}
		
		// User
		result.put("user_email", event.getRemoteUser().getEmail());
		result.put("user_name", event.getRemoteUser().getFullName());
		
		// Project
		result.put("project_name", event.getIssue().getProjectObject().getName());
		result.put("project_url", this.baseUrl + "/browse/" + event.getIssue().getProjectObject().getKey());
		
		// Issue
		addIssueData(result, event);
		
		// Event type
		addEventType(result, event);
		
		return result;
	}
	
	private void addIssueData(Map<String, String> result, IssueEvent event) {
		Issue issue = event.getIssue();
		
		// Basic data
		result.put("issue_type", issue.getIssueTypeObject().getName());
		result.put("issue_status", issue.getStatusObject().getName());
		
		result.put("issue_key", issue.getKey());
		result.put("issue_url", this.baseUrl + "/browse/" + issue.getKey());
		
		if (issue.getResolutionObject() != null) {
			result.put("issue_resolution", issue.getResolutionObject().getName());
		}
		
		// Fields
		if (issue.getAssignee() != null) {
			result.put("issue_assignee_name", issue.getAssignee().getFullName());
			result.put("issue_assignee_email", issue.getAssignee().getEmail());
		}
		
		if (issue.getReporter() != null) {
			result.put("issue_reporter_name", issue.getReporter().getFullName());
			result.put("issue_reporter_email", issue.getReporter().getEmail());
		}
		
		result.put("issue_summary", issue.getSummary());
		
		if (issue.getPriorityObject() != null) {
			result.put("issue_priority", issue.getPriorityObject().getName());
		}
		
		result.put("issue_votes", issue.getVotes().toString());
		result.put("issue_environment", issue.getEnvironment());
		
		if (issue.getDescription() != null) {
			result.put("issue_description", issue.getDescription());
		}
	}
	
	private void addEventType(HashMap<String, String> result, IssueEvent event) {
		Long eventTypeId = event.getEventTypeId();
		result.put("event_type_id", eventTypeId.toString()); // mostly for debugging purposes
		
		String eventType = "unknown";
		
		if (eventTypeId.equals(EventType.ISSUE_CREATED_ID)) {
			eventType = "create";
		} else if (eventTypeId.equals(EventType.ISSUE_COMMENTED_ID)) {
			eventType = "comment";
		} else if (eventTypeId.equals(EventType.ISSUE_UPDATED_ID)) {
			eventType = "update";
		} else if (eventTypeId.equals(EventType.ISSUE_ASSIGNED_ID)) {
			eventType = "assign";
		} else if (eventTypeId.equals(EventType.ISSUE_CLOSED_ID)) {
			eventType = "close";
		} else if (eventTypeId.equals(EventType.ISSUE_COMMENT_EDITED_ID)) {
			eventType = "edit_comment";
		} else if (eventTypeId.equals(EventType.ISSUE_DELETED_ID)) {
			eventType = "delete";
		} else if (eventTypeId.equals(EventType.ISSUE_MOVED_ID)) {
			eventType = "move";
		} else if (eventTypeId.equals(EventType.ISSUE_REOPENED_ID)) {
			eventType = "reopen";
		} else if (eventTypeId.equals(EventType.ISSUE_RESOLVED_ID)) {
			eventType = "resolve";
		} else if (eventTypeId.equals(EventType.ISSUE_WORKSTARTED_ID)) {
			eventType = "start_work";
		} else if (eventTypeId.equals(EventType.ISSUE_WORKSTOPPED_ID)) {
			eventType = "stop_work";
		}
		
		result.put("event_type", eventType);
		
	}
}
