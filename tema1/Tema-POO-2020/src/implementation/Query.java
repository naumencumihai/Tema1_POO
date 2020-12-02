package implementation;

import actor.ActorsAwards;
import entertainment.Season;
import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.Writer;
import org.json.simple.JSONObject;
import user.User;
import video.Movie;
import video.Show;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Collections;

import static utils.Utils.sortByKeyInteger;
import static utils.Utils.sortByValueInteger;
import static utils.Utils.calculateAverage;
import static utils.Utils.sortByValueDouble;
import static utils.Utils.sortByKeyDouble;
import static utils.Utils.stringToAwards;
import static utils.Utils.filterMovies;
import static utils.Utils.filterShows;


/**
 * This class handles the functionalities of the query action
 */
public class Query {

    /**
     * Returns users by number of ratings given
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    public static JSONObject numberofRatings(final Writer filewriter,
                                             final ActionInputData action,
                                             final Database database)
            throws IOException {
        ArrayList<String> list = new ArrayList<>();
        int number = action.getNumber();
        HashMap<String, Integer> usersByRating = new HashMap<>();

        for (User user : database.userList) {
            usersByRating.put(user.getUsername(), user.getRatings().size());
        }

        usersByRating = sortByKeyInteger(usersByRating,
                action.getSortType());
        usersByRating = sortByValueInteger(usersByRating,
                action.getSortType());

        int count = 0;

        for (Map.Entry<String, Integer> entry : usersByRating.entrySet()) {
            if (count == number) {
                break;
            }
            if (entry.getValue() != 0) {
                list.add(entry.getKey());
                count++;
            }
        }

        return filewriter.writeFile(action.getActionId(), "field",
                "Query result: " + list);
    }

    /**
     * Return Actors by average of ratings
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    public static JSONObject average(final Writer filewriter,
                                     final ActionInputData action,
                                     final Database database)
            throws IOException {
        ArrayList<String> list = new ArrayList<>();
        int number = action.getNumber();
        HashMap<String, Double> actorsByAverage = new HashMap<>();

        for (ActorInputData actor : database.actorList) {
            actorsByAverage.put(actor.getName(), calculateAverage(database, actor));
        }

        actorsByAverage = sortByKeyDouble(actorsByAverage,
                action.getSortType());
        actorsByAverage = sortByValueDouble(actorsByAverage,
                action.getSortType());

        int count = 0;

        for (Map.Entry<String, Double> entry : actorsByAverage.entrySet()) {
            if (count == number) {
                break;
            }
            if (entry.getValue() > 0) {
                list.add(entry.getKey());
                count++;
            }
        }
        return filewriter.writeFile(action.getActionId(), "field",
                "Query result: " + list);
    }

    /**
     * Return Actors by number of awards
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    public static JSONObject awards(final Writer filewriter,
                                    final ActionInputData action,
                                    final Database database)
            throws IOException {
        ArrayList<String> list = new ArrayList<>();
        List<String> queryAwards;
        List<ActorsAwards> actorAwards;
        List<ActorsAwards> awards = new ArrayList<>();
        queryAwards = action.getFilters().get(3); // List of awards

        // Converts strings from query to ActorAwards
        for (String s : queryAwards) {
            awards.add(stringToAwards(s));
        }

        Map<String, Integer> actorsByAwards = new HashMap<>();
        int totalAwards;

        for (ActorInputData actor : database.actorList) {

            totalAwards = 0;
            // Copies awards of current actor in actorAwards list
            actorAwards = new ArrayList<>(actor.getAwards().keySet());

            if (actorAwards.containsAll(awards)) {
                for (ActorsAwards award : actorAwards) {
                    totalAwards += actor.getAwards().get(award);
                }
                actorsByAwards.put(actor.getName(), totalAwards);
            }
        }

        actorsByAwards = sortByKeyInteger(actorsByAwards,
                action.getSortType());
        actorsByAwards = sortByValueInteger(actorsByAwards,
                action.getSortType());

        for (Map.Entry<String, Integer> entry : actorsByAwards.entrySet()) {
            if (entry.getValue() != 0) {
                list.add(entry.getKey());
            }
        }

        return filewriter.writeFile(action.getActionId(), "field",
                "Query result: " + list);
    }

    /**
     * Return actors by keywords in description
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    public static JSONObject filterDescription(final Writer filewriter,
                                               final ActionInputData action,
                                               final Database database)
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
            if (check) {
                list.add(actor.getName());
            }
        }

        // Sorts actor list according to query specification
        if (action.getSortType().equals("asc")) {
            Collections.sort(list);
        } else {
            Collections.sort(list, Collections.reverseOrder());
        }

        return filewriter.writeFile(action.getActionId(), "field",
                "Query result: " + list);
    }

    /**
     * Return Videos by ratings
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    public static JSONObject ratings(final Writer filewriter,
                                     final ActionInputData action,
                                     final Database database)
            throws IOException {
        int number = action.getNumber();
        Map<String, Double> videosByRating = new HashMap<>();
        double average, seasonAverage;
        List<String> list = new ArrayList<>();
        int count = 0;


        if (action.getObjectType().equals("movies")) {
            List<Movie> movieList = filterMovies(database.movieList, action);
            for (Movie movie : movieList) {
                average = 0.0;
                for (double rating : movie.getRatings()) {
                    average += rating;
                }
                average /= movie.getRatings().size();
                videosByRating.put(movie.getTitle(), average);
            }
        } else if (action.getObjectType().equals("shows")) {
            List<Show> showList = filterShows(database.showList, action);
            for (Show show : showList) {
                average = 0.0;
                for (Season s : show.getSeasons()) {
                    seasonAverage = 0.0;
                    for (double rating : s.getRatings()) {
                        seasonAverage += rating;
                    }
                    seasonAverage /= s.getRatings().size();
                    average += seasonAverage;
                }
                average /= show.getNumberofSeasons();
                videosByRating.put(show.getTitle(), average);
            }
        }

        videosByRating = sortByKeyDouble(videosByRating,
                action.getSortType());
        videosByRating = sortByValueDouble(videosByRating,
                action.getSortType());

        for (Map.Entry<String, Double> entry : videosByRating.entrySet()) {
            if (count == number) {
                break;
            }
            if (entry.getValue() > 0.0) {
                list.add(entry.getKey());
                count++;
            }
        }
        return filewriter.writeFile(action.getActionId(), "field",
                "Query result: " + list);
    }

    /**
     * Returns videos by number of favorites
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    public static JSONObject videoFavorite(final Writer filewriter,
                                           final ActionInputData action,
                                           final Database database)
            throws IOException {
        int number = action.getNumber();
        int count;
        Map<String, Integer> videosByFavorite = new HashMap<>();
        List<String> list = new ArrayList<>();

        if (action.getObjectType().equals("movies")) {
            List<Movie> movieList = filterMovies(database.movieList, action);
            for (Movie movie : movieList) {
                count = 0;
                for (User user : database.userList) {
                    if (user.getFavoriteMovies().contains(movie.getTitle())) {
                        count++;
                    }
                }
                videosByFavorite.put(movie.getTitle(), count);
            }
        } else if (action.getObjectType().equals("shows")) {
            List<Show> showList = filterShows(database.showList, action);
            for (Show show : showList) {
                count = 0;
                for (User user : database.userList) {
                    if (user.getFavoriteMovies().contains(show.getTitle())) {
                        count++;
                    }
                }
                videosByFavorite.put(show.getTitle(), count);
            }
        }

        videosByFavorite = sortByKeyInteger(videosByFavorite,
                action.getSortType());
        videosByFavorite = sortByValueInteger(videosByFavorite,
                action.getSortType());

        count = 0;
        for (Map.Entry<String, Integer> entry : videosByFavorite.entrySet()) {
            if (count == number) {
                break;
            }
            if (entry.getValue() > 0.0) {
                list.add(entry.getKey());
                count++;
            }
        }
        return filewriter.writeFile(action.getActionId(), "field",
                "Query result: " + list);
    }

    /**
     * Returns videos by duration
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    public static JSONObject longest(final Writer filewriter,
                                     final ActionInputData action,
                                     final Database database)
            throws IOException {
        int number = action.getNumber();
        int count = 0;
        Map<String, Integer> videosByDuration = new HashMap<>();
        List<String> list = new ArrayList<>();

        if (action.getObjectType().equals("movies")) {
            List<Movie> movieList = filterMovies(database.movieList, action);
            for (Movie movie : movieList) {
                videosByDuration.put(movie.getTitle(), movie.getDuration());
            }
        } else if (action.getObjectType().equals("shows")) {
            List<Show> showList = filterShows(database.showList, action);
            int total = 0;
            for (Show show : showList) {
                for (Season s : show.getSeasons()) {
                    total += s.getDuration();
                }
                videosByDuration.put(show.getTitle(), total);
            }
        }

        videosByDuration = sortByKeyInteger(videosByDuration,
                action.getSortType());
        videosByDuration = sortByValueInteger(videosByDuration,
                action.getSortType());

        for (Map.Entry<String, Integer> entry : videosByDuration.entrySet()) {
            if (count == number) {
                break;
            }
            list.add(entry.getKey());
            count++;
        }

        return filewriter.writeFile(action.getActionId(), "field",
                "Query result: " + list);
    }

    /**
     * Return videos by number of views
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    public static JSONObject mostViewed(final Writer filewriter,
                                        final ActionInputData action,
                                        final Database database)
            throws IOException {
        int number = action.getNumber();
        int count = 0;
        int views;
        List<String> list = new ArrayList<>();
        Map<String, Integer> videosByViews = new HashMap<>();

        if (action.getObjectType().equals("movies")) {
            List<Movie> movieList = filterMovies(database.movieList, action);
            for (Movie movie : movieList) {
                views = 0;
                for (User user : database.userList) {
                    if (user.getHistory().containsKey(movie.getTitle())) {
                        views += user.getHistory().get(movie.getTitle());
                    }
                }
                if (views != 0) {
                    videosByViews.put(movie.getTitle(), views);
                }
            }
        } else if (action.getObjectType().equals("shows")) {
            List<Show> showList = filterShows(database.showList, action);
            for (Show show : showList) {
                views = 0;
                for (User user : database.userList) {
                    if (user.getHistory().containsKey(show.getTitle())) {
                        views += user.getHistory().get(show.getTitle());
                    }
                }
                if (views != 0) {
                    videosByViews.put(show.getTitle(), views);
                }
            }
        }
        videosByViews = sortByKeyInteger(videosByViews,
                action.getSortType());
        videosByViews = sortByValueInteger(videosByViews,
                action.getSortType());

        for (Map.Entry<String, Integer> entry : videosByViews.entrySet()) {
            if (count == number) {
                break;
            }
            list.add(entry.getKey());
            count++;
        }

        return filewriter.writeFile(action.getActionId(), "field",
                "Query result: " + list);
    }
}
