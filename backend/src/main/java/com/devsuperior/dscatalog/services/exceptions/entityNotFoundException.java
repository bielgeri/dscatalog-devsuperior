package com.devsuperior.dscatalog.services.exceptions;

public class entityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public entityNotFoundException(String msg) {
		super(msg);
	}

}
