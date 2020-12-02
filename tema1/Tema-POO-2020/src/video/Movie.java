package video;

import java.util.ArrayList;
import java.util.List;

public class Movie extends Video {

    private int duration;

    private List<Double> ratings;

    public Movie() {
        ratings = new ArrayList<>();
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

    public void addRating(Double rating) {
        this.ratings.add(rating);
    }

    public double getRating() {
        double rating = 0.0;

        if (ratings.size() != 0) {
            for (double r : ratings)
                rating += r;

            rating /= ratings.size();
        }
        return rating;
    }
}