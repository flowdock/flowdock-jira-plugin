package com.flowdock.plugins.jira.config;

import com.atlassian.jira.project.Project;

public class ApiKeyPair {
	private Project project;
	private String projectKey; // when Project is not available, use this as an ID
	private String apiKey; // Flowdock API token
	
	public ApiKeyPair() {
	}
	
	public ApiKeyPair(Project project, String key) {
		this.setProject(project);
		this.setApiKey(key);
	}
	
	public ApiKeyPair(String projectKey, String apiKey) {
		this.projectKey = projectKey;
		this.setApiKey(apiKey);
	}

	public void setProject(Project project) {
		this.project = project;
		this.projectKey = project.getKey();
	}

	public Project getProject() {
		return this.project;
	}

	public void setApiKey(String key) {
		if (key == null || key == "") {
			this.apiKey = null;
		} else {
			this.apiKey = key;
		}
	}

	public String getApiKey() {
		return apiKey;
	}
	
	public String getProjectKey() {
		return this.projectKey;
	}
}
