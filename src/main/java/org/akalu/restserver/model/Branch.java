package org.akalu.restserver.model;


import java.util.List;



/**
 * A POJO class, holds info about Branch in a Git repository
 *
 * @author Alexey Kalutov
 * @since 0.0.1
 */
public class Branch extends GitObject {

	private static final long serialVersionUID = 3899649469154606565L;
	private String name;
	private List<Commit> commits;
	
	public Branch(String sha, String name, List<Commit> commits){
		super(sha);
		this.name = name;
		this.commits = commits;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Commit> getCommits() {
		return commits;
	}

	public void setCommits(List<Commit> commits) {
		this.commits = commits;
	}
	
}


