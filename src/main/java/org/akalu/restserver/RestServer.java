package org.akalu.restserver;

/**
 * This class implements a simple RESTful web service with 5 endpoints.
 * 
 * @author Alexey Kalutov
 * @since 0.0.3
 * 
 */

import static spark.Spark.*;
import static spark.Spark.get;
import java.io.IOException;

//import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;



import org.akalu.restserver.GitRepos;
import org.akalu.restserver.StatusCode;
import org.akalu.restserver.model.Message;
import org.akalu.restserver.JsonUtils;


public class RestServer{
	
  private GitRepos stack;
    
 
  public static void main(String[] args)  throws IOException, GitAPIException  {

	GitRepos stack = new GitRepos();

    port(Integer.valueOf(System.getenv("PORT")));
    staticFileLocation("/public");

    /**
     *  For testing server availability, returns status code 200 and message in the form of 
     *  serialized object of type Message
     */
   get("/test", (request, response) -> {
        response.status(StatusCode.HTTP_OK);
        response.type("application/json");
        return JsonUtils.dataToJson(new Message("Server online"));
    });
  

    /**
     * 	Returns info about cloned repos on the server
     */
    
    get("/", (request, response) -> {
        response.status(StatusCode.HTTP_OK);
        response.type("application/json");
        return JsonUtils.dataToJson(stack.getGitStackStatus());
    });

    /**
     * With parameter url - the server clones the git repository which url points to,
     *  makes it active and returns general info about it
	 *
     */
    get("/repo", (request, response) -> {
    	String url = request.queryParams("url");
    	if (stack.clonerepo(url)){
    		response.status(StatusCode.HTTP_OK);
    		response.type("application/json");
    		return JsonUtils.dataToJson(stack.getCurRepoInfo());
    	}
    	response.status(StatusCode.HTTP_INTERNAL_ERROR);
        return JsonUtils.dataToJson(new Message("Error during git clone"));
    });

    /**
     * 	Returns list of  all branches in the form of serialized objects of type Branch
     *  
     *  @see org.akalu.restserver.model.Branch
     */
    get("/repo/branches", (request, response) -> {
    	if (!stack.isStackEmpty()){
    		response.status(StatusCode.HTTP_OK);
    		response.type("application/json");
    		return JsonUtils.dataToJson(stack.getBranches());
    	}
    	response.status(StatusCode.HTTP_NOT_FOUND);
        return JsonUtils.dataToJson(new Message("No active repositories"));
    });

    /**
     * 	With required parameter filename - the server search for a file by name
     *  and returns its content
     */
    get("/content", (request, response) -> {
    	String fname = request.queryParams("filename"); 
    	if (fname != null){
    		String content = stack.getFilebyName(fname);
    		response.status(StatusCode.HTTP_OK);
    		response.type("application/json");
    		return content;
    	}
    	response.status(StatusCode.HTTP_NOT_FOUND);
        return JsonUtils.dataToJson(new Message("Nothing found"));
    });
    
    /**
     * 	Errors processing 	
     */
    
    exception(IllegalArgumentException.class, (e, request, response) -> {
    	response.status(StatusCode.HTTP_BAD_REQUEST);
    	response.body(JsonUtils.dataToJson(new Message(e.getMessage())));
    	});

    exception(IOException.class, (e, request, response) -> {
    	response.status(StatusCode.HTTP_BAD_REQUEST);
       	response.body(JsonUtils.dataToJson(new Message(e.getMessage())));
       	});
    
    }
// end of class
}
