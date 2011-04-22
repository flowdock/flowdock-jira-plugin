package com.flowdock.plugins.jira.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.atlassian.jira.config.properties.PropertiesManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.opensymphony.module.propertyset.PropertySet;

public class FlowdockConfigurationManager {
	public static final String FLOWDOCK_API_KEYS = "ext.flowdock.api.keys";
	
	private ProjectManager projectManager;
	
	public FlowdockConfigurationManager() {
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
		Properties props = this.readApiKeyPropertiesObj();
		
		List<Project> projects = this.projectManager.getProjectObjects();
		for (Project project : projects) {	
			String apiKey = props.getProperty(project.getKey());
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
		Properties props = new Properties();
		
		for (ApiKeyPair pair : apiKeys) {
			props.put(pair.getProjectKey(), pair.getApiKey());
		}
		
		OutputStream out = new ByteArrayOutputStream();
		try {
			props.store(out, null);
		} catch (IOException ioe) {} // seems unlikely to happen
		
		// Store properties to PropertySet
		PropertySet PS = PropertiesManager.getInstance().getPropertySet();
		try { PS.remove(FLOWDOCK_API_KEYS); } catch (Exception e) {} // Cannot overwrite pre-existing keys
		
		PS.setText(FLOWDOCK_API_KEYS, out.toString());
	}
	
	public String getApiKeyForProject(Project project) {
		List<ApiKeyPair> pairs = this.getFlowdockApiKeys();
		for (ApiKeyPair pair : pairs) {
			if (pair.getProject() == project) {
				return pair.getApiKey();
			}
		}
		
		return null;
	}
	
	// Bean configuration

	public void setProjectManager(ProjectManager manager) {
		this.projectManager = manager;
	}
	
	// Helpers
	
	private Properties readApiKeyPropertiesObj() {
		PropertySet PS = PropertiesManager.getInstance().getPropertySet();
		String propsString = PS.getText(FLOWDOCK_API_KEYS);
		if (propsString == null) propsString = ""; // initially it doesn't exist
		
		Properties props = new Properties();
		InputStream in = new ByteArrayInputStream(propsString.getBytes());
		
		try {
			props.load(in);
		} catch (IOException ioe) {} // seems unlikely to happen
		
		return props;
	}
}
