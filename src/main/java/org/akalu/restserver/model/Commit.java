package org.akalu.restserver.model;

import java.util.List;



/**
 * A POJO class, holds info about Commit in a Git repository
 *
 * @author Alexey Kalutov
 * @since 0.0.2
 */
public class Commit extends GitObject {
	private static final long serialVersionUID = 8765789944191931199L;
	
	private String commiter;
	private String message;
	private Integer commitTime; 
	private List<TreeNode> trees;
	
	public Commit(String sha, String commiter, String msg, Integer commitTime, List<TreeNode> trees){
		super(sha);
		this.commiter = commiter;
		this.message = msg;
		this.commitTime = commitTime;
		this.trees = trees;
	}

	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	public List<TreeNode> getTrees() {
		return trees;
	}

	public void setTrees(List<TreeNode> trees) {
		this.trees = trees;
	}


	public String getCommiter() {
		return commiter;
	}


	public void setCommiter(String commiter) {
		this.commiter = commiter;
	}


	public Integer getCommitTime() {
		return commitTime;
	}


	public void setCommitTime(Integer commitTime) {
		this.commitTime = commitTime;
	}
	
}



