package com.flowdock.plugins.jira.config;

import java.util.List;

import com.atlassian.jira.web.action.JiraWebActionSupport;

public class ConfigureFlowdockAction extends JiraWebActionSupport {
	private static final long serialVersionUID = 8900385397054039775L;
	
	private FlowdockConfigurationManager flowdockConfigurationManager;
	private List<ApiKeyPair> apiKeyPairs;
	
	public List<ApiKeyPair> getApiKeyPairs() {
		return apiKeyPairs;
	}

	public void setApiKeyPairs(List<ApiKeyPair> apiKeyPairs) {
		this.apiKeyPairs = apiKeyPairs;
	}

	public ConfigureFlowdockAction() {
	}

	public String doDefault() {
		this.updateTemplateData();
		return INPUT;
	}
	
	/**
	 * Bean configuration.
	 * 
	 * @param manager
	 */
	public void setFlowdockConfigurationManager(FlowdockConfigurationManager manager) {
		this.flowdockConfigurationManager = manager;
	}
	
	private void updateTemplateData() {
		this.apiKeyPairs = this.flowdockConfigurationManager.getFlowdockApiKeys();
	}
}
