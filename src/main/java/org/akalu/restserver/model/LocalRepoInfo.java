package org.akalu.RestServer.model;

/**
 * This class is a simple POJO to hold general information about git repository
 * Used for creating an informative json responses
 * 
 * @author Alexey Kalutov
 * @version 0.0.1
 */

public class LocalRepoInfo {
	private String localDir;
	private String originUrl;

	public LocalRepoInfo(String dir, String originUrl) {
		this.localDir = dir;
		this.originUrl = originUrl;
	}

	public LocalRepoInfo() {
	}

	public String getDir() {
		return localDir;
	}

	public void setDir(String dir) {
		this.localDir = dir;
	}

	public String getUrl() {
		return originUrl;
	}

	public void setUrl(String url) {
		this.originUrl = url;
	}
}
