package video;

import java.util.ArrayList;
import java.util.List;

public class Movie extends Video {

    private int duration;

    private List<Double> ratings;

    public Movie() {
    }

    public Movie(String title, int year, ArrayList<String> genres, ArrayList<String> cast, int duration, List<Double> ratings) {
        super(title, year, genres, cast);
        this.duration = duration;
        this.ratings = ratings;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<Double> getRatings() {
        return ratings;
    }

    public void setRatings(List<Double> ratings) {
        this.ratings = ratings;
    }
}