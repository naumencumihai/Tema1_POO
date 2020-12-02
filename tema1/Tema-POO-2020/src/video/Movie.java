package video;

import java.util.ArrayList;
import java.util.List;

public final class Movie extends Video {

    private int duration;

    private List<Double> ratings;

    public Movie() {
        ratings = new ArrayList<>();
    }

    public Movie(final String title, final  int year, final  ArrayList<String> genres,
                 final ArrayList<String> cast, final  int duration,
                 final List<Double> ratings) {
        super(title, year, genres, cast);
        this.duration = duration;
        this.ratings = ratings;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(final int duration) {
        this.duration = duration;
    }

    public List<Double> getRatings() {
        return ratings;
    }

    public void setRatings(final List<Double> ratings) {
        this.ratings = ratings;
    }

    /**
     * Rating
     * @param rating
     */
    public void addRating(final Double rating) {
        this.ratings.add(rating);
    }

    /**
     * Return average of ratings
     * @return
     */
    public double getRating() {
        double rating = 0.0;

        if (ratings.size() != 0) {
            for (double r : ratings) {
                rating += r;
            }
            rating /= ratings.size();
        }
        return rating;
    }
}
