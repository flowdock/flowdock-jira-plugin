package com.flowdock.plugins.jira;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class FlowdockConnection {
	
	private static final Logger log = Logger.getLogger(FlowdockConnection.class);
	
	public static void sendApiMessage(final Map<String, String> params, String apiKey) {
		if (params == null || apiKey == null) {
			return;
		}
		
		try {
			final URL apiUrl = getApiUrl(apiKey);
			
			// Run HTTP POST call in a dedicated thread to avoid blocking and
			// slowing down Jira.
			Thread postThread = new Thread() {
				public void run() {
					try {
						log.debug(String.format("Starting HTTP POST call to %s", apiUrl));
						postData(apiUrl, params);
					} catch (Exception e) {
						log.error(String.format("Error at HTTP POST call to %s", apiUrl));
						log.error(e);
					}
				}
			};
			postThread.start();
		} catch (MalformedURLException mue) {
			
		} catch (IOException ioe) {
			
		}
	}
	
	private static void postData(URL apiUrl, Map<String, String> params) throws IOException {
		HttpURLConnection conn = (HttpURLConnection)apiUrl.openConnection();
		conn.setRequestMethod("POST");
		conn.setUseCaches(false);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestProperty("User-Agent", "Flowdock Jira Plugin");
		
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		String postData = mapToPostData(params);
		out.write(postData);
		out.close();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while (in.readLine() != null);
		in.close();
		
		conn.connect();
	}
	
	private static String mapToPostData(Map<String, String> map) throws UnsupportedEncodingException {
		Iterator<Entry<String, String>> iter = map.entrySet().iterator();
		StringBuffer sb = new StringBuffer();
		while (iter.hasNext()) {
			Entry<String, String> keyval = iter.next();
			String key = keyval.getKey();
			if (keyval.getValue() == null) continue; // ignore null values
			String encodedValue = URLEncoder.encode(keyval.getValue(), "UTF-8");
			
			sb.append(key);
			sb.append("=");
			sb.append(encodedValue);
			
			if (!iter.hasNext()) break;
			
			sb.append("&");
		}
		return sb.toString();
	}
	
	private static URL getApiUrl(String apiKey) throws MalformedURLException {
		return new URL("https://api.flowdock.com/v1/jira/" + apiKey);
	}
}
