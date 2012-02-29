package com.flowdock.plugins.jira.config;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.config.properties.PropertiesManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.opensymphony.module.propertyset.PropertySet;

public class FlowdockConfigurationManager {
	public static final String FLOWDOCK_API_KEYS_PREFIX = "ext.flowdock.api.keys.";
	
	private ProjectManager projectManager;
	private PropertySet propertySet;
	
	public FlowdockConfigurationManager(ProjectManager manager) {
		this.projectManager = manager;
		this.propertySet = this.initPropertySet();
	}
	
	/**
	 * Returns a Flowdock ApiKeyPair for each Project in the system.
	 * 
	 * {@link ApiKeyPair#getApiKey()} might be null, if it hasn't been
	 * configured for the given project.
	 * 
	 * @return
	 */
	public synchronized List<ApiKeyPair> getFlowdockApiKeys() {
		List<ApiKeyPair> result = new ArrayList<ApiKeyPair>();
		
		List<Project> projects = this.projectManager.getProjectObjects();
		for (Project project : projects) {	
			String apiKey = this.getApiKeyForProject(project);
			result.add(new ApiKeyPair(project, apiKey));
		}
		
		return result;
	}
	
	/**
	 * Save listed ApiKeys.
	 * 
	 * @param apiKeys
	 */
	public synchronized void setFlowdockApiKeys(List<ApiKeyPair> apiKeys) {
		for (ApiKeyPair pair : apiKeys) {
			this.setApiKeyForProject(pair.getProjectKey(), pair.getApiKey());
		}
	}
	
	public String getApiKeyForProject(Project project) {
		String apiKey = this.propertySet.getText(FLOWDOCK_API_KEYS_PREFIX + project.getKey());
		return apiKey;
	}
	
	private void setApiKeyForProject(String projectKey, String apiKey) {
		String propertyKey = FLOWDOCK_API_KEYS_PREFIX + projectKey;
		
		try { this.propertySet.remove(propertyKey); } catch (Exception e) {} // Cannot overwrite pre-existing keys
		
		this.propertySet.setText(propertyKey, apiKey);
	}
	
	// Bean configuration

	public void setProjectManager(ProjectManager manager) {
		this.projectManager = manager;
	}

	private PropertySet initPropertySet() {
		return PropertiesManager.getInstance().getPropertySet();
	}
}
