package org.akalu.RestServer;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;

import org.eclipse.jgit.internal.storage.file.FileRepository;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import org.akalu.RestServer.model.*;
import org.akalu.RestServer.JsonUtils;



/**
 * This class is intended to hold a metainfo about every local git repository, perform git ops on them (stack management)
 * 
 * 
 * @author Alexey Kalutov
 * @version 0.0.1
 */

public class GitRepos {
	private Integer totalRepos;
	private Integer innerId;
	private GitRepoDescr curGit;
	private Map <String,GitRepoDescr> mapRepo;
	private ArrayList<GitRepoDescr> stack;
	private final String tempDir = "/home";

	public GitRepos(){
		totalRepos = 0;
		innerId = 0;
		curGit = null;
		mapRepo = new HashMap <String,GitRepoDescr>();
		stack = new ArrayList<GitRepoDescr>();
	}
	
public Boolean isStackEmpty(){
	return  (stack.size() == 0)?true:false;
}

public Boolean clonerepo(String uri) throws IOException, GitAPIException{
	
	if (uri == null){
		if (curGit != null) return true;
		return false;
	}
	// do not clone the from the same uri twice
	if (mapRepo.keySet().contains(uri)){
		curGit = mapRepo.get(uri);
		return true;
	}
	
    File localPath = File.createTempFile(tempDir, "");
    
    if(!localPath.delete()) {
        throw new IOException("Could not delete temporary file " + localPath);
    }
    
	Git git = Git.cloneRepository().setURI(uri).setDirectory(localPath).call();
	Repository localRepo = git.getRepository();
	curGit = new GitRepoDescr(innerId++,uri,localPath,git,localRepo);
	stack.add(curGit);
	totalRepos++;
	mapRepo.put(uri, curGit);
	return true;
}

/*
 * Returns info about all cloned repos
 * 
 */
public List<LocalRepoInfo> getGitStackStatus(){
	List<LocalRepoInfo> stackstatus = new ArrayList<LocalRepoInfo>();
	
	for (GitRepoDescr grd: stack){
		stackstatus.add(new LocalRepoInfo(grd.localPath.toString(),grd.url));
	}
	return stackstatus;
}

/*
 * Returns info about current (active) repository
 * 
 */

public LocalRepoInfo getCurRepoInfo() throws IOException, GitAPIException{
	LocalRepoInfo lri = new LocalRepoInfo();
	if (curGit == null) return lri;
	lri.setDir(curGit.localPath.toString());
	lri.setUrl(curGit.url);
	return lri;
}

public List<Commit> getCommits(Ref head)  throws IOException, GitAPIException{
	
	List<Commit> lc = new ArrayList<Commit>();
    try (RevWalk walk = new RevWalk(curGit.repo)) {
    	RevCommit commit1 = walk.parseCommit(head.getObjectId());

    	walk.markStart(commit1);
    	for (RevCommit rev : walk) {
    		lc.add(new Commit(rev.getId().getName(),rev.getShortMessage(),null));
    	}

    	walk.dispose();
    }
    return lc;
}

public List<Branch> getBranches()  throws IOException, GitAPIException{
	List<Branch> lb = new ArrayList<Branch>();
	if (curGit == null) return lb;
	List<Ref> call = curGit.git.branchList().setListMode(ListMode.ALL).call();
    for (Ref ref : call) {
    	lb.add(new Branch(ref.getObjectId().getName(), ref.getName(), getCommits(ref)));
    }
	return lb;
}

public String getFilebyName(String name) throws IOException, GitAPIException{
	String text = "";
	if (curGit== null) return  text;   
	Repository localRepo = curGit.repo;
    ObjectId lastCommitId = localRepo.resolve(Constants.HEAD);

    // a RevWalk allows to walk over commits based on some filtering that is defined
    try (RevWalk revWalk = new RevWalk(localRepo)) {
        RevCommit commit = revWalk.parseCommit(lastCommitId);
        // and using commit's tree find the path
        RevTree tree = commit.getTree();

        // now try to find a specific file
        try (TreeWalk treeWalk = new TreeWalk(localRepo)) {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(name));
            if (!treeWalk.next()) {
            	throw new IOException("File not found");
            }

            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader loader = localRepo.open(objectId);

            // loader reads the file
            text = new String(loader.getCachedBytes(), "UTF-8");
        }

        revWalk.dispose();
    }
    return text;

}

/*
 *  inner class to describe a git repository
 */
class GitRepoDescr{
    public Integer id;
    public String url;
    public File localPath;
    public Git git;
    public Repository repo;
    
    public GitRepoDescr(Integer id, String url, File localPath, Git git, Repository repo){
    	this.id = id;
    	this.url = url;
    	this.localPath = localPath;
    	this.git = git;
    	this.repo = repo;
    }
    
}



}
