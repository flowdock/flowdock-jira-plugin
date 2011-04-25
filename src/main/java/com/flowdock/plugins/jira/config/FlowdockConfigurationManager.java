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
	
	public FlowdockConfigurationManager(ProjectManager manager) {
		this.projectManager = manager;
	}
	
	/**
	 * Returns a Flowdock ApiKeyPair for each Project in the system.
	 * 
	 * {@link ApiKeyPair#getApiKey()} might be null, if it hasn't been
	 * configured for the given project.
	 * 
	 * @return
	 */
	public List<ApiKeyPair> getFlowdockApiKeys() {
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
	public void setFlowdockApiKeys(List<ApiKeyPair> apiKeys) {
		for (ApiKeyPair pair : apiKeys) {
			this.setApiKeyForProject(pair.getProjectKey(), pair.getApiKey());
		}
	}
	
	public String getApiKeyForProject(Project project) {
		PropertySet PS = PropertiesManager.getInstance().getPropertySet();
		String apiKey = PS.getText(FLOWDOCK_API_KEYS_PREFIX + project.getKey());
		return apiKey;
	}
	
	public synchronized void setApiKeyForProject(String projectKey, String apiKey) {
		String propertyKey = FLOWDOCK_API_KEYS_PREFIX + projectKey;
		
		PropertySet PS = PropertiesManager.getInstance().getPropertySet();
		try { PS.remove(propertyKey); } catch (Exception e) {} // Cannot overwrite pre-existing keys
		
		PS.setText(propertyKey, apiKey);
	}
	
	// Bean configuration

	public void setProjectManager(ProjectManager manager) {
		this.projectManager = manager;
	}
}
