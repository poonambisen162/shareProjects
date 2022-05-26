package com.poonam.compareface;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.CompareFacesRequest;
import software.amazon.awssdk.services.rekognition.model.CompareFacesResponse;
import software.amazon.awssdk.services.rekognition.model.CompareFacesMatch;
import software.amazon.awssdk.services.rekognition.model.ComparedFace;
import software.amazon.awssdk.core.SdkBytes;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.rekognition.model.DetectFacesRequest;
import software.amazon.awssdk.services.rekognition.model.DetectFacesResponse;
import software.amazon.awssdk.services.rekognition.model.FaceDetail;
import software.amazon.awssdk.services.rekognition.model.Attribute;

/*
 * class FaceProcessor is responsible for making calls to AWT Rekognition APIs
 */
@Component
public class FaceProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(FaceProcessor.class);

	/*
	 * This method compares two images
	 * @param images. contains the names of images to compare
	 * @param baseDir. Directory path where these files exist
	 * @param similarityThreshold. Minimum confidence percentage with which the images should be matched
	 * @return ImagesToCompare object along with match percentage updated
	 */
	public static ImagesToCompare Compare(ImagesToCompare images, String baseDir, Float similarityThreshold) throws IncorrectFaceCountException, FileNotFoundException, FaceProcessorInternalErrorException{     
		LOGGER.debug("Compare starts");
		LOGGER.info("Compare parameter :: baseDir::"+baseDir+" similarityThreshold::"+similarityThreshold);
		String sourceImage = baseDir+images.getSourceImage();
		String targetImage = baseDir+images.getTargetImage();

		RekognitionClient rekClient = getRekognitionClient();
		try {

			// Create an Image object for the source image.
			Image souImage = FaceProcessor.getImageForRekognition(sourceImage);

			Image tarImage = FaceProcessor.getImageForRekognition(targetImage);
			
			int facesCountSource = FaceProcessor.detectNumberOfFacesinImage(rekClient, souImage );
			LOGGER.info(facesCountSource+" Face/s found in source image");
			int facesCountTarget = FaceProcessor.detectNumberOfFacesinImage(rekClient, tarImage );
			LOGGER.info(facesCountTarget+" Face/s found in target image");
			if(facesCountSource==1 && facesCountTarget == 1) {
				float percentage = FaceProcessor.compareTwoFaces(rekClient, similarityThreshold, souImage, tarImage);
				rekClient.close();
				images.setMatchPercentage(percentage);
				LOGGER.debug("Compare :: ends");
				return images;
			}
			else {
				String errorMsg="";
				if(facesCountSource<1) {
					errorMsg+= "No face detected in source image\n";
				}else if(facesCountSource>1){
					errorMsg+= "Multiple faces detected in source image\n";
				}
				
				if(facesCountTarget<1) {
					errorMsg+= "No face detected in target image\n";
				}else if(facesCountTarget>1){
					errorMsg+= "Multiple faces detected in target image\n";
				}
				LOGGER.error("Incorrect number of faces detected in images:: \n"+errorMsg);
				throw new IncorrectFaceCountException(errorMsg);
				
			}
		}catch ( FileNotFoundException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}catch(RekognitionException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
			throw new FaceProcessorInternalErrorException(e.getMessage(), e);
		}
	}
	
	/*
	 *  Create an Image object for the source image.
	 *  @param imagePath. Path of the file containing image
	 *  @return Image object require for the aws Rekognition APIs 
	 */
	private static Image getImageForRekognition(String imagePath) throws FileNotFoundException {
		InputStream imageStream = new FileInputStream(imagePath);
		SdkBytes imageBytes = SdkBytes.fromInputStream(imageStream);
		
		// Create an Image object for the source image.
		Image image = Image.builder().bytes(imageBytes).build();
		return image;
	}
	
	/*
	 * get RekognitionClient to call aws API on
	 */
	private static RekognitionClient getRekognitionClient() {
		Region region = Region.US_EAST_1;
		RekognitionClient rekClient = RekognitionClient.builder()
				.region(region)
				.credentialsProvider(ProfileCredentialsProvider.create())
				.build();
		return rekClient;
	}

	/*
	 * Compares the two images for face match.
	 * @param rekClient. RekognitionClient
	 * @param similarityThreshold. Minimum confidence percentage with which the images should be matched
	 * @param souImage. Image object of source image
	 * @param tarImage. Image object of target image
	 * @return confidence percentage with which the images matched
	 */
	private static float compareTwoFaces(RekognitionClient rekClient, Float similarityThreshold,
			Image souImage, Image tarImage) throws FileNotFoundException, RekognitionException {
		 
		LOGGER.debug("compareTwoFaces :: start");
		CompareFacesRequest facesRequest = CompareFacesRequest.builder().sourceImage(souImage).targetImage(tarImage)
				.similarityThreshold(similarityThreshold).build();

		// Compare the two images.
		float matchPercetage = 0.0f;
		CompareFacesResponse compareFacesResult = rekClient.compareFaces(facesRequest);
		List<CompareFacesMatch> faceDetails = compareFacesResult.faceMatches();
		if(faceDetails.size()==0) {
			matchPercetage = 0.0f;
		}
		for (CompareFacesMatch match: faceDetails){
			ComparedFace face= match.face();
			matchPercetage = face.confidence();
		}
		LOGGER.debug("compareTwoFaces :: end");
		return matchPercetage;
	}

	/*
	 * Detects number of faces in a picture
	 * @param rekClient. RekognitionClient
	 * @param souImage. Image object of the image in which faces to be detected
	 * @return number of faces detected
	 */
	private static int detectNumberOfFacesinImage(RekognitionClient rekClient, Image souImage)
			throws FileNotFoundException, RekognitionException {
		LOGGER.debug("detectNumberOfFacesinImage :: start");

		DetectFacesRequest facesRequest = DetectFacesRequest.builder().attributes(Attribute.ALL).image(souImage)
				.build();

		DetectFacesResponse facesResponse = rekClient.detectFaces(facesRequest);
		List<FaceDetail> faceDetails = facesResponse.faceDetails();
		LOGGER.debug("detectNumberOfFacesinImage :: ends");
		return faceDetails.size();

	}

}