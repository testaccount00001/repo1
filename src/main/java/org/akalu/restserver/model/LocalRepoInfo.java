package org.akalu.restserver.model;

import java.io.Serializable;

/**
 * This class is a simple POJO to hold general information about git repository
 * Used for creating an informative JSON responses
 * 
 * @author Alexey Kalutov
 * @version 0.0.1
 */

public class LocalRepoInfo implements Serializable {
	private static final long serialVersionUID = -4077184960938795571L;

	private String localDir;
	private String originUrl;
	private String owner;
	private String email;

	public LocalRepoInfo(String dir, String originUrl, String owner, String email) {
		this.localDir = dir;
		this.originUrl = originUrl;
		this.owner = owner;
		this.email = email;
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
