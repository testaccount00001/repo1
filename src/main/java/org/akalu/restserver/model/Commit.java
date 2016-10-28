package org.akalu.RestServer.model;

import java.util.List;



/**
 * A POJO class, holds info about Commit in a Git repository
 *
 * @author Alexey Kalutov
 * @since 0.0.1
 */
public class Commit extends GitObject {
	private String message;
	private List<TreeNode> trees;
	
	public Commit(String sha, String msg, List<TreeNode> trees){
		super(sha);
		this.message = msg;
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
	
}



