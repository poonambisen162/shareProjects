package com.poonam.compareface.controllers;


import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.poonam.compareface.FaceProcessor;
import com.poonam.compareface.FaceProcessorInternalErrorException;
import com.poonam.compareface.ImagesToCompare;
import com.poonam.compareface.IncorrectFaceCountException;
/*
 *  CompareFacesController is the rest controller class for service /compare. It handles HTTP request and response for the service.
 *  /compare compares two pictures and determines if they are of the same person
 */
@RestController()
public class CompareFacesController {
	
	@Value("${compareFace.similarityThreshold}")
    Float similarityThreshold;

	@Value("${compareFace.imageBaseDirectory}")
	String baseDir;
	
	/*
	 * compareFace the main method of this class, It handles the POST request to service /compare
	 * @param images contains names of images to compare
	 * @return compared images
	 */
	@RequestMapping(value = "/compare",method = RequestMethod.POST)
	public ImagesToCompare compareFace(@RequestBody ImagesToCompare images) throws IncorrectFaceCountException,FaceProcessorInternalErrorException, FileNotFoundException {		
		return FaceProcessor.Compare(images, baseDir, similarityThreshold);
		
	}
	
	/*
	 * Exception handler for IncorrectFaceCountException. this exception is thrown when there are multiple faces 
	 * in source or target image
	 * @ return formatted error msg
	 */
	@ExceptionHandler
	public ResponseEntity<FaceProcessorErrorResponse> handleIncorrectFaceCountException(IncorrectFaceCountException exc) {
		
		FaceProcessorErrorResponse error = new FaceProcessorErrorResponse();
		
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setMessage(exc.getMessage());
		error.setTimeStamp(System.currentTimeMillis());
		
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	/*
	 * Exception handler for FileNotFoundException. this exception is thrown when any of the images specifies in request 
	 * doesn't exist in file system
	 * @ return formatted error msg
	 */
	@ExceptionHandler
	public ResponseEntity<FaceProcessorErrorResponse> handleFileNotFoundException(FileNotFoundException exc) {
		
		FaceProcessorErrorResponse error = new FaceProcessorErrorResponse();
		
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setMessage(exc.getMessage());
		error.setTimeStamp(System.currentTimeMillis());
		
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	/*
	 * Exception handler for FaceProcessorInternalErrorException. this exception is thrown when there is some internal error received
	 * aws API
	 * @ return formatted error msg
	 */
	@ExceptionHandler
	public ResponseEntity<FaceProcessorErrorResponse> handleFaceProcessorInternalErrorException(FaceProcessorInternalErrorException exc) {
		
		FaceProcessorErrorResponse error = new FaceProcessorErrorResponse();
		
		error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		error.setMessage(exc.getMessage());
		error.setTimeStamp(System.currentTimeMillis());
		
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}	
}
