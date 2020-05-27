package utils;

public class Tuple {
    private double score;
    private double magnitude;

    public Tuple(double score, double magnitude){
        this.magnitude = magnitude;
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }
}
