package com.poonam.compareface;

/*
 * Exception FaceProcessorInternalErrorException. this exception is thrown when there is some internal error received
 * aws API
 * this wrapper exception class for RekognitionException
 */
public class FaceProcessorInternalErrorException extends RuntimeException {

	private static final long serialVersionUID = 5547216666247542206L;

	public FaceProcessorInternalErrorException(String message, Throwable cause) {
		super(message, cause);
	}


}
