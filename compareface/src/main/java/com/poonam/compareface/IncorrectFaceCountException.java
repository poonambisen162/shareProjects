package com.poonam.compareface;
/*
 * Exception IncorrectFaceCountException. this exception is thrown when there are multiple faces 
 * in source or target image
 */
public class IncorrectFaceCountException extends RuntimeException {


	private static final long serialVersionUID = 8346609857814399675L;

	public IncorrectFaceCountException(String msg) {
		super(msg);
	}

}
