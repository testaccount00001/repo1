package org.akalu.restserver;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.*;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import org.akalu.restserver.model.*;



/**
 * This class is intended to hold a metainfo about every local git repository,
 *  perform git ops on them (stack management)
 * 
 * @author Alexey Kalutov
 * @since 0.0.1
 */

public class GitRepos {
	/** Total number of repos on the server*/
	private Integer totalRepos;
	/** Autoincremental counter*/
	private Integer innerId;
	
	private final String tempDir = "/home";

	/** Describes active (usually last cloned) repository */
	private GitRepoDescr curGit;
	
	/** Map: (url) -> (local repository descriptor) */
	private Map <String,GitRepoDescr> mapRepo;
	
	/** List of records describing every cloned repository */
	private ArrayList<GitRepoDescr> stack;
	
	/** 
	 * Non-arguments Constructor
	 */
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

/**
 * This method clones a remote git-repository to temporary local directory
 * <p>
 * Note: there is no authorization in this version, hence only public repositories available
 * @param uri - the URI of remote repository
 * @return true, if repository successfully cloned
 */

public Boolean clonerepo(String uri) throws IOException, GitAPIException{
	
	if (uri == null){
		if (curGit != null) return true;
		return false;
	}
	// do not clone the repository with the same uri twice
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

/**
 * Returns metainfo about all cloned repos on the server 
 * in the form of list of the records (InnerId, url, localpath, Git type variable, Repository type variable)
 *   
 * 
 */
public List<LocalRepoInfo> getGitStackStatus() throws IOException, GitAPIException{
	List<LocalRepoInfo> stackstatus = new ArrayList<LocalRepoInfo>();
	
	for (GitRepoDescr grd: stack){
		Config config = curGit.repo.getConfig();
	    String name = config.getString("user", null, "name");
	    String email = config.getString("user", null, "email");
		stackstatus.add(new LocalRepoInfo(grd.localPath.toString(),grd.url,name,email));
	}
	return stackstatus;
}

/**
 * Returns metainfo about current (active) repository
 * 
 */

public LocalRepoInfo getCurRepoInfo() throws IOException, GitAPIException{
	LocalRepoInfo lri = new LocalRepoInfo();

	if (curGit == null) return lri;
	Config config = curGit.repo.getConfig();
    String name = config.getString("user", null, "name");
    String email = config.getString("user", null, "email");

	lri.setDir(curGit.localPath.toString());
	lri.setUrl(curGit.url);
	lri.setOwner(name);
	lri.setEmail(email);
	
	return lri;
}

/**
 * Returns list of commits from branch referenced by {@code head}
 * 
 */

public List<Commit> getCommits(Ref head)  throws IOException, GitAPIException{
	
	List<Commit> lc = new ArrayList<Commit>();
    try (RevWalk walk = new RevWalk(curGit.repo)) {
    	RevCommit commit1 = walk.parseCommit(head.getObjectId());

    	walk.markStart(commit1);
    	for (RevCommit rev : walk) {
    		lc.add(new Commit(rev.getId().getName(),
    						rev.getAuthorIdent().getName(),
    						rev.getShortMessage(),
    						rev.getCommitTime(),
    						null));
    	}

    	walk.dispose();
    }
    return lc;
}

/**
 * Returns metainfo about current (active) repository
 * 
 */

public List<Branch> getBranches()  throws IOException, GitAPIException{
	List<Branch> lb = new ArrayList<Branch>();
	if (curGit == null) return lb;
	List<Ref> call = curGit.git.branchList().setListMode(ListMode.ALL).call();
    for (Ref ref : call) {
    	lb.add(new Branch(ref.getObjectId().getName(), ref.getName(), getCommits(ref)));
    }
	return lb;
}

/**
 * Search file by name and returns its content
 * <p>
 * Note: in this version the search is accomplished within the bounds of
 * last commit
 */


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

/**
 *  Inner class to describe a Git repository
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
