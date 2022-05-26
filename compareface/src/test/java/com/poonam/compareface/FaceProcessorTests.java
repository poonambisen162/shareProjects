package com.poonam.compareface;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;


@SpringBootTest
class FaceProcessorTests {
		
	String baseDir = "/usr/share/comparefaces/sampleFaces/";
	Float similarityThreshold = 70f;

	@Test
	void testCompareFacesMatch() {
		
		ImagesToCompare imagesToCompare = new ImagesToCompare();
		imagesToCompare.setSourceImage("profilepic.jpeg");
		imagesToCompare.setTargetImage("sameperson.jpeg");
		ImagesToCompare compareResult;
		try {
			compareResult = FaceProcessor.Compare(imagesToCompare,  baseDir, similarityThreshold);
			System.out.println("match percentage"+compareResult.getMatchPercentage());
			assertNotEquals(0.0f, compareResult.getMatchPercentage());
		} catch (IncorrectFaceCountException | FileNotFoundException | FaceProcessorInternalErrorException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		
	}
	@Test
	void testCompareFacesDontMatch() {
		
		ImagesToCompare imagesToCompare = new ImagesToCompare();
		imagesToCompare.setSourceImage("profilepic.jpeg");
		imagesToCompare.setTargetImage("anotherperson.jpeg");
		ImagesToCompare compareResult;
		try {
			compareResult = FaceProcessor.Compare(imagesToCompare, baseDir, similarityThreshold);
			System.out.println("match percentage"+compareResult.getMatchPercentage());
			assertEquals(0.0f, compareResult.getMatchPercentage(),0.000001f);
		} catch (IncorrectFaceCountException | FileNotFoundException | FaceProcessorInternalErrorException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	@Test
	void testCompareFacesMutlipleFaces() {
		
		Exception exception = assertThrows(IncorrectFaceCountException.class, () -> {	
			ImagesToCompare imagesToCompare = new ImagesToCompare();
			imagesToCompare.setSourceImage("profilepic.jpeg");
			imagesToCompare.setTargetImage("multiplefaces.jpeg");
			FaceProcessor.Compare(imagesToCompare,  baseDir, similarityThreshold);

		});
	    String expectedMessage = "Multiple faces detected in target image\n";
	    String actualMessage = exception.getMessage();
	    assertTrue(actualMessage.contains(expectedMessage));

	    
	    
	    
	}
	@Test
	void testCompareFacesIncorrectFile() {
		
		Exception exception = assertThrows(FileNotFoundException.class, () -> {	
			ImagesToCompare imagesToCompare = new ImagesToCompare();
			imagesToCompare.setSourceImage("profilepic.jpeg");
			imagesToCompare.setTargetImage("faceUnknown.jpeg");
			FaceProcessor.Compare(imagesToCompare,  baseDir, similarityThreshold);
		});
	    String expectedMessage = "No such file or directory";
	    String actualMessage = exception.getMessage();
	    assertTrue(actualMessage.contains(expectedMessage));

	}
}
