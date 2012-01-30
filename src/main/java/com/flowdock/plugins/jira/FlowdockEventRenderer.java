package com.flowdock.plugins.jira;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.util.JiraVelocityHelper;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.ComponentManager;
import org.ofbiz.core.entity.GenericValue;

import com.google.gson.Gson;

public class FlowdockEventRenderer {
	private String baseUrl;
	private JiraVelocityHelper jiraVelocityHelper;
	private JiraAuthenticationContext jiraAuthenticationContext;
	private I18nHelper i18nHelper;
	
	public FlowdockEventRenderer(final JiraAuthenticationContext jiraAuthenticationContext) {
		// Magic trick to get the JIRA baseUrl.
		this.baseUrl = ManagerFactory.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
		this.jiraVelocityHelper = new JiraVelocityHelper(ComponentManager.getInstance().getFieldManager());;
		this.jiraAuthenticationContext = jiraAuthenticationContext;
		this.i18nHelper = this.jiraAuthenticationContext.getI18nHelper();
	}
	
	public Map<String, String> renderEvent(IssueEvent event) {
		HashMap<String, String> result = new HashMap<String, String>();
		
		// Comment: optional
		if (event.getComment() != null) {
			result.put("comment_body", event.getComment().getBody());
		}
		
		// User
		result.put("user_email", event.getUser().getEmailAddress());
		result.put("user_name", event.getUser().getDisplayName());
		
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
			result.put("issue_assignee_name", issue.getAssigneeUser().getDisplayName());
			result.put("issue_assignee_email", issue.getAssigneeUser().getEmailAddress());
		}
		
		if (issue.getReporter() != null) {
			result.put("issue_reporter_name", issue.getReporterUser().getDisplayName());
			result.put("issue_reporter_email", issue.getReporterUser().getEmailAddress());
		}
		
		result.put("issue_summary", issue.getSummary());

		result.put("issue_changelog", getChangelog(event));
		
		if (issue.getPriorityObject() != null) {
			result.put("issue_priority", issue.getPriorityObject().getName());
		}
		
		result.put("issue_votes", issue.getVotes().toString());
		result.put("issue_environment", issue.getEnvironment());
		
		if (issue.getDescription() != null) {
			result.put("issue_description", issue.getDescription());
		}
	}

	private String getChangelog(IssueEvent event) {
		try {
			List<Map<String, String>> data = this.getChangelogArray(event);
			Gson gson = new Gson();
			String json = gson.toJson(data);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<Map<String, String>> getChangelogArray(IssueEvent event) throws Exception {
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();

		GenericValue changelog = event.getChangeLog();
		if (changelog == null || changelog.getRelated("ChildChangeItem") == null) return null;

		for (GenericValue changedItem : changelog.getRelated("ChildChangeItem")) {
			// This pattern is borrowed from jira-project/jira-components/jira-core/src/main/resources/templates/email/html/includes/fields/changelog.vm
			String oldStringKey, newStringKey;
			if (changedItem.getString("field") == "Comment") {
				oldStringKey = "oldvalue";
				newStringKey = "newvalue";
			} else {
				oldStringKey = "oldstring";
				newStringKey = "newstring";
			}

			String fieldName = this.jiraVelocityHelper.getFieldName(changedItem, this.i18nHelper);
			String oldValue = this.jiraVelocityHelper.getPrettyFieldString(changedItem.getString("field"), changedItem.getString(oldStringKey), this.i18nHelper);
			String newValue = this.jiraVelocityHelper.getPrettyFieldString(changedItem.getString("field"), changedItem.getString(newStringKey), this.i18nHelper);

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("field", fieldName);
			map.put("old_value", oldValue);
			map.put("new_value", newValue);

			result.add(map);
		}

		return result;
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
