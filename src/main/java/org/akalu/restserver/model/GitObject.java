package org.akalu.restserver.model;


import java.io.Serializable;

/**
 * A POJO class, holds rudimentary info about [any] Git-object in a Git repository
 *
 * @author Alexey Kalutov
 * @since 0.0.1
 */

public class GitObject implements Serializable {
	private static final long serialVersionUID = 1L;

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