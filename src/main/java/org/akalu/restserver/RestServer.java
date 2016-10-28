package org.akalu.RestServer;

/**
 * This class implements a simple web service with 5 endpoints.
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
import org.akalu.RestServer.model.Message;
import org.akalu.RestServer.model.LocalRepoInfo;
import org.akalu.RestServer.JsonUtils;


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
        return JsonUtils.dataToJson(new Message("Server online"));
    });
  

    // http GET methods
    
    get("/", (request, response) -> {
        response.status(StatusCode.HTTP_OK);
        response.type("application/json");
        return JsonUtils.dataToJson(stack.getGitStackStatus());
    });

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

    get("/repo/branches", (request, response) -> {
    	if (!stack.isStackEmpty()){
    		response.status(StatusCode.HTTP_OK);
    		response.type("application/json");
    		return JsonUtils.dataToJson(stack.getBranches());
    	}
    	response.status(StatusCode.HTTP_NOT_FOUND);
        return JsonUtils.dataToJson(new Message("No active repositories"));
    });

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
    
    // errors processing
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
