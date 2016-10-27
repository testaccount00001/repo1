package org.akalu.RestServer;

/**
 * This is a web service with  end points for access to.
 * 
 * @author Alexey Kalutov
 * @version 0.0.1
 */

import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.*;
import static spark.Spark.get;
import static spark.Spark.post;
import spark.Request;
import spark.Response;
import spark.Route;

import lombok.Data;

import java.io.IOException;

//import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;



import org.akalu.RestServer.GitRepos;
import org.akalu.RestServer.StatusCode; 

public class RestServer{
	
  private GitRepos stack;
    
 
  public static void main(String[] args)  throws IOException, GitAPIException  {

	GitRepos stack = new GitRepos();

    port(Integer.valueOf(System.getenv("PORT")));
    staticFileLocation("/public");

    // for testing purposes
    get("/test", (request, response) -> {
        response.status(StatusCode.HTTP_OK);
        response.type("application/json");
        return "{}";
    });
  
    // get methods
    get("/repo", (request, response) -> {
    	String url = request.queryParams("url");
//    	String url = "https://github.com/testaccount00001/repo1";
    	if (stack.clonerepo(url)){
    		response.status(StatusCode.HTTP_OK);
    		response.type("application/json");
    		return stack.getCurRepoInfo();
    	}
	response.status(StatusCode.HTTP_INTERNAL_ERROR);
    return "{}";
    });
    
    get("/content", (request, response) -> {
    	String fname = request.queryParams("filename"); 
    	if (fname != null){
    		String content = stack.getFilebyName(fname);
    		response.status(StatusCode.HTTP_OK);
    		response.type("application/json");
    		return content;
    	}
	response.status(StatusCode.HTTP_INTERNAL_ERROR);
    return "{}";
    });
    
    }
// end of class
}
