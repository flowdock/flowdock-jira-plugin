package com.flowdock.plugins.jira.config;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class ConfigureFlowdockAction extends JiraWebActionSupport {
	private static final long serialVersionUID = 8900385397054039775L;
	
	private FlowdockConfigurationManager flowdockConfigurationManager;
	private List<ApiKeyPair> apiKeyPairs;
	
	// POST data
	private String[] projectKeys;
	private String[] apiKeys;
	
	public List<ApiKeyPair> getApiKeyPairs() {
		return apiKeyPairs;
	}

	public void setApiKeyPairs(List<ApiKeyPair> apiKeyPairs) {
		this.apiKeyPairs = apiKeyPairs;
	}

	public ConfigureFlowdockAction() {
	}
	
	public String doExecute() {
		this.updateTemplateData();
		return INPUT;
	}
	
	@RequiresXsrfCheck
	public String doSave() {
		List<ApiKeyPair> pairs = this.parseApiKeyPairs();
		this.flowdockConfigurationManager.setFlowdockApiKeys(pairs);
		
		return this.getRedirect("configureFlowdock.jspa");
	}
	
	private List<ApiKeyPair> parseApiKeyPairs() {
		List<ApiKeyPair> result = new ArrayList<ApiKeyPair>();
		if (this.projectKeys == null || this.apiKeys == null || this.projectKeys.length != this.apiKeys.length) {
			return result;
		}
		
		for (int i=0; i<this.projectKeys.length; i++) {
			if (this.apiKeys[i] != null && this.apiKeys[i] != "") {
				result.add(new ApiKeyPair(this.projectKeys[i], this.apiKeys[i]));
			}
		}
		
		return result;
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
	
	public void setProjectKeys(String[] projectKeys) {
		this.projectKeys = projectKeys;
	}
	
	public void setApiKeys(String[] apiKeys) {
		this.apiKeys = apiKeys;
	}
}
