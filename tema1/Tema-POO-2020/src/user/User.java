package user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {

    private String username;

    private String subscriptionType;

    private Map<String, Integer> history;

    private ArrayList<String> favoriteMovies;

    private Map<String, Double> ratings;

    public User() {
        this.ratings = new HashMap<>();
    }

    public User(String username, String subscriptionType,
                    Map<String, Integer> history, ArrayList<String> favoriteMovies,
                    Map<String, Double> ratings) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.history = history;
        this.favoriteMovies = favoriteMovies;
        this.ratings = ratings;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public void setHistory(Map<String, Integer> history) {
        this.history = history;
    }

    public ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void setFavoriteMovies(ArrayList<String> favoriteMovies) {
        this.favoriteMovies = favoriteMovies;
    }

    public Map<String, Double> getRatings() {
        return ratings;
    }

    public void setRatings(Map<String, Double> ratings) {
        this.ratings = ratings;
    }

    public void addFavoriteMovie(String title) {
        this.favoriteMovies.add(title);
    }

    public void addRating(String title, Double rating) {
        this.ratings.put(title, rating);
    }

}
