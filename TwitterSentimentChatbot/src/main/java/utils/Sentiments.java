package utils;

public class Sentiments {

	public enum SentimentName {VERY_POSITIVE, POSITIVE, NEUTRAL, NEGATIVE, VERY_NEGATIVE}

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


	public static SentimentName classify(double score){
		score = Math.round(score * 10.0) / 10.0;
		if(score <= -0.6){
			return SentimentName.VERY_NEGATIVE;
		}else if(score <= -0.2){
			return SentimentName.NEGATIVE;
		}else if(score <= 0.1){
			return SentimentName.NEUTRAL;
		}else if(score <= 0.5){
			return SentimentName.POSITIVE;
		}else if(score <= 1){
			return SentimentName.VERY_POSITIVE;
		}else{
			return null;
		}
	}

		
}
