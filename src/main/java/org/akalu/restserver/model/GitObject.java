package org.akalu.RestServer.model;






/**
 * A POJO class, holds info about Tree in a Git repository
 *
 * @author Alexey Kalutov
 * @since 0.0.1
 */

public class GitObject {
	private String sha;
	
	public GitObject(String sha){
		this.sha = sha;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

}