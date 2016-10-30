package org.akalu.restserver.model;

import java.io.Serializable;

/**
 * A POJO class, holds a [string] message
 *
 * @author Alexey Kalutov
 * @since 0.0.1
 */
public class Message implements Serializable{

	private static final long serialVersionUID = -7647619618070649931L;

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
