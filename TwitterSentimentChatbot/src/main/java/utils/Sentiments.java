package utils;

public class Sentiments {

	public float score;
	public float magnitude;
	
	public Sentiments(float score, float magnitude) {
		this.score = score;
		this.magnitude = magnitude;
	}
	
	public Sentiments() {

	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	public float getMagnitude() {
		return magnitude;
	}
	public void setMagnitude(float magnitude) {
		this.magnitude = magnitude;
	}
		
}
