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
import java.util.*;

import static utils.Utils.*;

/**
 * This class handles the functionalities of the query action
 */
public class Query {

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
