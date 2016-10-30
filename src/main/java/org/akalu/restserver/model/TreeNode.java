package org.akalu.restserver.model;


import java.util.List;


/**
 * A POJO class, holds info about Tree in a Git repository
 *
 * @author Alexey Kalutov
 * @since 0.0.1
 */

public class TreeNode extends GitObject {

	private static final long serialVersionUID = -5009443763524056115L;
	private List<GitObject> nodes;
	
	public TreeNode(String sha, List<GitObject> nodes){
		super(sha);
		this.nodes = nodes;
	}
	

	public List<GitObject> getNodes() {
		return nodes;
	}

	public void setNodes(List<GitObject> nodes) {
		this.nodes = nodes;
	}

}

