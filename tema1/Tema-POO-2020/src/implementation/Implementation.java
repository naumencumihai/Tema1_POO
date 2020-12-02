package implementation;

import actor.ActorsAwards;
import common.Constants;
import entertainment.Season;
import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.Writer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import user.User;
import video.Movie;
import video.Show;

import java.io.IOException;
import java.util.*;

import static utils.Utils.*;
import static utils.Utils.sortByValueInteger;

public class Implementation extends Recommendation{

    public Implementation () {
    }

    public void Implementaion (Writer filewriter, Database database, JSONArray result) throws IOException {

        for (ActionInputData action : database.actionList) {

            if (action.getActionType().equals(Constants.COMMAND)) {
                if (action.getType().equals("favorite"))
                    result.add(this.Favorite(filewriter,action, database));
                else if (action.getType().equals("view"))
                    result.add(this.View(filewriter, action, database));
                else
                    result.add(this.Rating(filewriter, action, database));
            }
            else if (action.getActionType().equals(Constants.QUERY)) {
                switch (action.getObjectType()) {
                    case "users":
                        result.add(this.NumberofRatings
                                (filewriter, action, database));
                        break;
                    case "movies":
                    case "shows":
                        switch (action.getCriteria()) {
                            case "ratings" -> result.add(this.Ratings
                                    (filewriter, action, database));
                            case "favorite" -> result.add(this.VideoFavorite
                                    (filewriter, action, database));
                            case "longest" -> result.add(this.Longest
                                    (filewriter, action, database));
                            case "most_viewed" -> result.add(this.MostViewed
                                    (filewriter, action, database));
                        }
                        break;
                    case "actors":
                        switch (action.getCriteria()) {
                            case "average" ->
                                    result.add(this.Average
                                            (filewriter, action, database));
                            case "awards" ->
                                    result.add(this.Awards
                                            (filewriter, action, database));
                            case "filter_description" ->
                                    result.add(this.FilterDescription
                                            (filewriter, action, database));
                        }
                        break;
                }
            }
            else if (action.getActionType().equals(Constants.RECOMMENDATION)) {
                switch (action.getType()) {
                    case "standard" ->
                            result.add(this.Standard
                                    (filewriter, action, database));

                    case "best_unseen" ->
                            result.add(this.BestUnseen
                                    (filewriter, action, database));
                    case "search" ->
                            result.add(this.Search
                                    (filewriter, action, database));
                    /*
                    case "popular" ->
                            result.add(this.Popular
                                    (filewriter, action, database));
                    case "favorite" ->
                            result.add(this.RecommendationFavorite
                                    (filewriter, action, database));
                    */
                }
            }
        }
    }
/*
Commands
 */
    // Favorite command
    public JSONObject Favorite (Writer filewriter, ActionInputData action,
                                Database database) throws IOException {
        User currentUser = database.userMap.get(action.getUsername());
        String video = action.getTitle();
        Map<String, Integer> history = currentUser.getHistory();

        if (currentUser.getFavoriteMovies().contains(video)){
            return  filewriter.writeFile(action.getActionId(), "field",
                    "error -> " + video + " is already in favourite list");
        }
        else if(history.containsKey(video)){
            currentUser.addFavoriteMovie(video);

            return filewriter.writeFile(action.getActionId(), "field",
                    "success -> " + video + " was added as favourite");
        }
        else {
            return filewriter.writeFile(action.getActionId(), "field",
                    "error -> " + video + " is not seen");
        }
    }

    // View command
    public JSONObject View (Writer filewriter, ActionInputData action,
                            Database database) throws IOException {
        User currentUser = database.userMap.get(action.getUsername());
        String video = action.getTitle();
        Map<String, Integer> history = currentUser.getHistory();

        /*
            Checks if video is already viewed, and if true
            it increments the views number with one, else
            it adds a new entry to User's history
         */
        if (history.containsKey(video)) {
            history.replace(video, history.get(video) + 1);
        }
        else {
            history.put(video, 1);
        }
        currentUser.setHistory(history);
        return filewriter.writeFile(action.getActionId(),
                "field", "success -> " + video +
                        " was viewed with total views of " + history.get(video));
    }

    // Rating command
    public JSONObject Rating (Writer filewriter, ActionInputData action,
                              Database database) throws IOException {
        User currentUser = database.userMap.get(action.getUsername());
        String title = action.getTitle();

        // Checks if video is viewed by user
        if (currentUser.getHistory().containsKey(title)) {

            // Checks if video is already rated
            if (currentUser.getRatings() != null &&
                currentUser.getRatings().containsKey(title) &&
                (!database.showMap.containsKey(title))) {
                return filewriter.writeFile(action.getActionId(),
                        "field", "error -> " + title +
                                " has been already rated");
            }
            else {
                // Specified video is a Movie
                if (database.movieMap.containsKey(title)) {
                    currentUser.addRating(title, action.getGrade());
                    Movie movie = database.movieMap.get(title);

                    // Updates Movie's ratings
                    movie.addRating(action.getGrade());
                }
                // Specified video is a Show
                else if (database.showMap.containsKey(title)) {
                    currentUser.addRating(title, action.getGrade());
                    Show show = database.showMap.get(title);

                    // Season number
                    int snumber = action.getSeasonNumber() - 1;

                    // Updates Season's ratings
                    List<Double> ratings;
                    ratings = show.getSeasons().get(snumber).getRatings();
                    ratings.add(action.getGrade());
                    show.getSeasons().get(snumber).setRatings(ratings);

                }

                return filewriter.writeFile(action.getActionId(),
                        "field", "success -> " + title +
                                " was rated with " + action.getGrade() +
                                " by " + currentUser.getUsername());
            }
        }
        return filewriter.writeFile(action.getActionId(),
                "field", "error -> " + title +
                        " is not seen");
    }
/*
Query
 */
// Returns users by number of ratings given
public JSONObject NumberofRatings (Writer filewriter, ActionInputData action,
                                   Database database) throws IOException {
    ArrayList<String> list = new ArrayList<>();
    int number = action.getNumber();
    HashMap<String, Integer> users_by_rating = new HashMap<>();

    for (User user : database.userList) {
        users_by_rating.put(user.getUsername(), user.getRatings().size());
    }

    users_by_rating = sortByKeyInteger(users_by_rating, action.getSortType());
    users_by_rating = sortByValueInteger(users_by_rating, action.getSortType());

    int count = 0;

    for (Map.Entry<String, Integer> entry : users_by_rating.entrySet()) {
        if (count == number) break;
        if (entry.getValue() != 0) {
            list.add(entry.getKey());
            count++;
        }
    }

    return filewriter.writeFile(action.getActionId(), "field",
            "Query result: " + list);
}

    public JSONObject Average (Writer filewriter, ActionInputData action,
                               Database database) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        int number = action.getNumber();
        HashMap<String, Double> actors_by_average = new HashMap<>();

        for (ActorInputData actor : database.actorList) {
            actors_by_average.put(actor.getName(), calculateAverage(database, actor));
        }

        actors_by_average = sortByKeyDouble(actors_by_average, action.getSortType());
        actors_by_average = sortByValueDouble(actors_by_average, action.getSortType());

        int count = 0;

        for (Map.Entry<String, Double> entry : actors_by_average.entrySet()) {
            if (count == number) break;
            if (entry.getValue() > 0) {
                list.add(entry.getKey());
                count++;
            }
        }
        return filewriter.writeFile(action.getActionId(), "field",
                "Query result: " + list);
    }

    public JSONObject Awards (Writer filewriter, ActionInputData action,
                              Database database) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        List<String> query_awards;
        List<ActorsAwards> actor_awards;
        List<ActorsAwards> awards = new ArrayList<>();
        query_awards = action.getFilters().get(3); // List of awards

        // Converts strings from query to ActorAwards
        for (String s : query_awards) {
            awards.add(stringToAwards(s));
        }

        Map<String, Integer> actors_by_awards = new HashMap<>();
        int total_awards;

        for (ActorInputData actor : database.actorList) {

            total_awards = 0;
            // Copies awards of current actor in actor_awards list
            actor_awards = new ArrayList<>(actor.getAwards().keySet());

            if (actor_awards.containsAll(awards)) {
                for (ActorsAwards award : actor_awards) {
                    total_awards += actor.getAwards().get(award);
                }
                actors_by_awards.put(actor.getName(), total_awards);
            }
        }

        actors_by_awards = sortByKeyInteger(actors_by_awards, action.getSortType());
        actors_by_awards = sortByValueInteger(actors_by_awards, action.getSortType());

        for (Map.Entry<String, Integer> entry : actors_by_awards.entrySet()) {
            if (entry.getValue() != 0) {
                list.add(entry.getKey());
            }
        }

        return filewriter.writeFile(action.getActionId(), "field",
                "Query result: " + list);
    }

    public JSONObject FilterDescription (Writer filewriter,
                                         ActionInputData action,
                                         Database database)
            throws IOException {
        // List to be returned
        ArrayList<String> list = new ArrayList<>();
        // List with keywords from query
        List<String> words;
        words = action.getFilters().get(2);
        // Career description of an actor
        String career;
        // Used to check if keywords are in career description
        boolean check;
        // Iterates through all actors
        for (ActorInputData actor : database.actorList) {
            career = actor.getCareerDescription();
            // Makes it case insesitive
            career = career.toLowerCase();

            // Default value
            check = true;

            // Iterates through keywords
            for (String word : words) {
                word  = word.toLowerCase();
                word = " " + word; // Adds space in front of word
                /*
                If at least one keyword not in career description,
                breaks from for loop and check becomes false
                */
                if (!career.contains(word)) {
                    check = false;
                    break;
                }
            }
            // Adds only if all keywords in actor description
            if (check)
                list.add(actor.getName());
        }

        // Sorts actor list according to query specification
        if (action.getSortType().equals("asc"))
            Collections.sort(list);
        else
            Collections.sort(list, Collections.reverseOrder());

        return filewriter.writeFile(action.getActionId(), "field",
                "Query result: " + list);
    }

    public JSONObject Ratings (Writer filewriter, ActionInputData action,
                               Database database) throws IOException {
        int number = action.getNumber();
        Map<String, Double> videos_by_rating = new HashMap<>();
        double average, season_average;
        List<String> list = new ArrayList<>();
        int count = 0;


        if (action.getObjectType().equals("movies")) {
            List<Movie> movieList = FilterMovies(database.movieList, action);
            for (Movie movie : movieList) {
                average = 0.0;
                for (double rating : movie.getRatings()) {
                    average += rating;
                }
                average /= movie.getRatings().size();
                videos_by_rating.put(movie.getTitle(), average);
            }
        }
        else if (action.getObjectType().equals("shows")) {
            List<Show> showList = FilterShows(database.showList, action);
            for (Show show : showList) {
                average = 0.0;
                for (Season s : show.getSeasons()) {
                    season_average = 0.0;
                    for (double rating : s.getRatings()) {
                        season_average += rating;
                    }
                    season_average /= s.getRatings().size();
                    average += season_average;
                }
                average /= show.getNumberofSeasons();
                videos_by_rating.put(show.getTitle(), average);
            }
        }

        videos_by_rating = sortByKeyDouble(videos_by_rating, action.getSortType());
        videos_by_rating = sortByValueDouble(videos_by_rating, action.getSortType());

        for (Map.Entry<String, Double> entry : videos_by_rating.entrySet()) {
            if (count == number) break;
            if (entry.getValue() > 0.0) {
                list.add(entry.getKey());
                count++;
            }
        }
        return filewriter.writeFile(action.getActionId(), "field",
                "Query result: " + list);
    }

    public JSONObject VideoFavorite (Writer filewriter,
                                     ActionInputData action,
                                     Database database) throws IOException {
        int number = action.getNumber();
        int count;
        Map<String, Integer> videos_by_favorite = new HashMap<>();
        List<String> list = new ArrayList<>();

        if (action.getObjectType().equals("movies")) {
            List<Movie> movieList = FilterMovies(database.movieList, action);
            for (Movie movie : movieList) {
                count = 0;
                for (User user : database.userList) {
                    if (user.getFavoriteMovies().contains(movie.getTitle()))
                        count++;
                }
                videos_by_favorite.put(movie.getTitle(), count);
            }
        }
        else if (action.getObjectType().equals("shows")) {
            List<Show> showList = FilterShows(database.showList, action);
            for (Show show : showList) {
                count = 0;
                for (User user : database.userList) {
                    if (user.getFavoriteMovies().contains(show.getTitle()))
                        count++;
                }
                videos_by_favorite.put(show.getTitle(), count);
            }
        }

        videos_by_favorite = sortByKeyInteger(videos_by_favorite, action.getSortType());
        videos_by_favorite = sortByValueInteger(videos_by_favorite, action.getSortType());

        count = 0;
        for (Map.Entry<String, Integer> entry : videos_by_favorite.entrySet()) {
            if (count == number) break;
            if (entry.getValue() > 0.0) {
                list.add(entry.getKey());
                count++;
            }
        }
        return filewriter.writeFile(action.getActionId(), "field",
                "Query result: " + list);
    }

    public JSONObject Longest (Writer filewriter,
                               ActionInputData action,
                               Database database) throws IOException {
        int number = action.getNumber();
        int count = 0;
        Map<String, Integer> videos_by_duration = new HashMap<>();
        List<String> list = new ArrayList<>();

        if (action.getObjectType().equals("movies")) {
            List<Movie> movieList = FilterMovies(database.movieList, action);
            for (Movie movie : movieList) {
                videos_by_duration.put(movie.getTitle(), movie.getDuration());
            }
        }
        else if (action.getObjectType().equals("shows")) {
            List<Show> showList = FilterShows(database.showList, action);
            int total = 0;
            for (Show show : showList) {
                for (Season s : show.getSeasons()) {
                    total += s.getDuration();
                }
                videos_by_duration.put(show.getTitle(), total);
            }
        }

        videos_by_duration = sortByKeyInteger(videos_by_duration, action.getSortType());
        videos_by_duration = sortByValueInteger(videos_by_duration, action.getSortType());

        for (Map.Entry<String, Integer> entry : videos_by_duration.entrySet()) {
            if (count == number) break;
            list.add(entry.getKey());
            count++;
        }

        return filewriter.writeFile(action.getActionId(), "field",
                "Query result: " + list);
    }

    public JSONObject MostViewed (Writer filewriter,
                                  ActionInputData action,
                                  Database database) throws IOException {
        int number = action.getNumber();
        int count = 0;
        int views;
        List<String> list = new ArrayList<>();
        Map<String, Integer> videos_by_views = new HashMap<>();

        if (action.getObjectType().equals("movies")) {
            List<Movie> movieList = FilterMovies(database.movieList, action);
            for (Movie movie : movieList) {
                views = 0;
                for (User user : database.userList) {
                    if (user.getHistory().containsKey(movie.getTitle()))
                        views += user.getHistory().get(movie.getTitle());
                }
                if (views != 0)
                    videos_by_views.put(movie.getTitle(), views);
            }
        }
        else if (action.getObjectType().equals("shows")) {
            List<Show> showList = FilterShows(database.showList, action);
            for (Show show : showList) {
                views = 0;
                for (User user : database.userList) {
                    if (user.getHistory().containsKey(show.getTitle()))
                        views += user.getHistory().get(show.getTitle());
                }
                if (views != 0)
                    videos_by_views.put(show.getTitle(), views);
            }
        }
        videos_by_views = sortByKeyInteger(videos_by_views, action.getSortType());
        videos_by_views = sortByValueInteger(videos_by_views, action.getSortType());

        for (Map.Entry<String, Integer> entry : videos_by_views.entrySet()) {
            if (count == number) break;
            list.add(entry.getKey());
            count++;
        }

        return filewriter.writeFile(action.getActionId(), "field",
                "Query result: " + list);
    }
}
