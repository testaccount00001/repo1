package org.akalu.RestServer.model;




/**
 * A POJO class, holds a [string] message
 *
 * @author Alexey Kalutov
 * @since 0.0.1
 */
public class Message{
	private String message;
	
	public Message(String msg){
		this.message =  msg;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String msg) {
		this.message = msg;
	}

}
