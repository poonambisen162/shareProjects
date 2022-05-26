package com.poonam.compareface;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.poonam.compareface.controllers.FaceProcessorErrorResponse;


@SpringBootTest
public class CompareFacesControllerTest {
	String baseUrl = "http://localhost:8080/compareface/compare";
	@Test
	void testMatchedImages() {
		RestTemplate restTemplate = new RestTemplate();
		ImagesToCompare imagesToCompare = new ImagesToCompare();
		imagesToCompare.setSourceImage("profilepic.jpeg");
		imagesToCompare.setTargetImage("sameperson.jpeg");
		ImagesToCompare comparedImages = restTemplate.postForObject(baseUrl, imagesToCompare, ImagesToCompare.class);
		assertNotNull(comparedImages);
		assertNotEquals(0.0f, comparedImages.getMatchPercentage());
	}
	
	@Test
	void testNotMatchedImages() {
		RestTemplate restTemplate = new RestTemplate();
		ImagesToCompare imagesToCompare = new ImagesToCompare();
		imagesToCompare.setSourceImage("profilepic.jpeg");
		imagesToCompare.setTargetImage("anotherperson.jpeg");
		ImagesToCompare comparedImages = restTemplate.postForObject(baseUrl, imagesToCompare, ImagesToCompare.class);
		assertNotNull(comparedImages);
		assertEquals(0.0f, comparedImages.getMatchPercentage(),0.000001f);
	}
	
	@Test
	void testMultipleFacesImages() {
		RestTemplate restTemplate = new RestTemplate();
		ImagesToCompare imagesToCompare = new ImagesToCompare();
		imagesToCompare.setSourceImage("profilepic.jpeg");
		imagesToCompare.setTargetImage("multiplefaces.jpeg");
		Exception exception = assertThrows(HttpClientErrorException.BadRequest.class, () -> {
		ResponseEntity<FaceProcessorErrorResponse> result = restTemplate.postForEntity(baseUrl, imagesToCompare, FaceProcessorErrorResponse.class);

		});
	    String expectedMessage = "Multiple faces detected in target image";
	    String actualMessage = exception.getMessage();
	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void testImageDoesNotExist() {
		RestTemplate restTemplate = new RestTemplate();
		ImagesToCompare imagesToCompare = new ImagesToCompare();
		imagesToCompare.setSourceImage("profilepic.jpeg");
		imagesToCompare.setTargetImage("faceunknown.jpeg");
		Exception exception = assertThrows(HttpClientErrorException.BadRequest.class, () -> {
		ResponseEntity<FaceProcessorErrorResponse> result = restTemplate.postForEntity(baseUrl, imagesToCompare, FaceProcessorErrorResponse.class);

		});
	    String expectedMessage = "No such file or directory";
	    String actualMessage = exception.getMessage();
	    assertTrue(actualMessage.contains(expectedMessage));
	}

}
