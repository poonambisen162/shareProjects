package com.poonam.compareface;
/*
 * This class is the request and response POJO for service /compare
 * when used as request it contains source images name and target image name
 * and when used a response it contains source images name and target image name along with the match percentage
 * match percentage is the confidence percentage with which the images match
 */
public class ImagesToCompare {
	private String sourceImage;
	private String targetImage;
	private float matchPercentage = 0.0f;

	public float getMatchPercentage() {
		return matchPercentage;
	}

	public void setMatchPercentage(float matchPercentage) {
		this.matchPercentage = matchPercentage;
	}

	public String getSourceImage() {
		return sourceImage;
	}

	public void setSourceImage(String sourceImage) {
		this.sourceImage = sourceImage;
	}

	public String getTargetImage() {
		return targetImage;
	}

	public void setTargetImage(String targetImage) {
		this.targetImage = targetImage;
	}

}
