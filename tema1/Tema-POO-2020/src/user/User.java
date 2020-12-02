package user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class User {

    private String username;

    private String subscriptionType;

    private Map<String, Integer> history;

    private ArrayList<String> favoriteMovies;

    private Map<String, Double> ratings;

    public User() {
        this.ratings = new HashMap<>();
    }

    public User(final String username, final String subscriptionType,
                final Map<String, Integer> history,
                final ArrayList<String> favoriteMovies,
                final Map<String, Double> ratings) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.history = history;
        this.favoriteMovies = favoriteMovies;
        this.ratings = ratings;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setSubscriptionType(final String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public void setHistory(final Map<String, Integer> history) {
        this.history = history;
    }

    public ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void setFavoriteMovies(final ArrayList<String> favoriteMovies) {
        this.favoriteMovies = favoriteMovies;
    }

    public Map<String, Double> getRatings() {
        return ratings;
    }

    public void setRatings(final Map<String, Double> ratings) {
        this.ratings = ratings;
    }

    /**
     * Adds a favorite Movie
     * @param title
     */
    public void addFavoriteMovie(final String title) {
        this.favoriteMovies.add(title);
    }

    /**
     * Adds a rating
     * @param title
     * @param rating
     */
    public void addRating(final String title, final Double rating) {
        this.ratings.put(title, rating);
    }
}
