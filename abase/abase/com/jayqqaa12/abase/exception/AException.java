package com.jayqqaa12.abase.exception;

public class AException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public AException() {
		super();
	}
	
	public AException(String msg) {
		super(msg);
	}
	
	public AException(Throwable ex) {
		super(ex);
	}
	
	public AException(String msg,Throwable ex) {
		super(msg,ex);
	}

}
