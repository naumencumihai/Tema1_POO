package implementation;

import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONObject;
import video.Movie;
import video.Show;

import java.io.IOException;
import java.util.*;

import static utils.Utils.*;

public class Recommendation {

    public JSONObject Standard (Writer filewriter, ActionInputData action,
                                       Database database) throws IOException {
        String video = "All videos watched";
        List<String> list = new ArrayList<>();
        List<String> history_list = new ArrayList<>();
        Map<String, Integer> history;

        for (Movie movie : database.movieList)
            list.add(movie.getTitle());
        for (Show show : database.showList)
            list.add(show.getTitle());

        // Gets User's history
        history = database.userMap.get(action.getUsername()).getHistory();

        // Creates a list with titles from history
        for (Map.Entry<String, Integer> entry : history.entrySet())
            history_list.add(entry.getKey());

        // Searches for first non-viewed item
        for (String title : list) {
            if (!history_list.contains(title)) {
                video = title;
                break;
            }
        }

        return filewriter.writeFile(action.getActionId(), "field",
                "StandardRecommendation result: " + video);
    }

    public JSONObject BestUnseen (Writer filewriter, ActionInputData action,
                                       Database database) throws IOException {
        String video = "All videos watched";
        List<String> list = new ArrayList<>();
        List<String> history_list = new ArrayList<>();
        Map<String, Integer> history;
        Map<String, Double> videos_by_rating = new LinkedHashMap<>();

        // Gets User's history
        history = database.userMap.get(action.getUsername()).getHistory();

        // Creates a list with titles from history
        for (Map.Entry<String, Integer> entry : history.entrySet())
            history_list.add(entry.getKey());

        // Populates videos_by_rating Map
        for (Movie movie : database.movieList)
            videos_by_rating.put(movie.getTitle(), movie.getRating());

        for (Show show : database.showList)
            videos_by_rating.put(show.getTitle(), show.getRating());


        // Sorts by ratings in descending order
        videos_by_rating = sortByValueDouble(videos_by_rating, "desc");

        // Creates list from Map
        for (Map.Entry<String, Double> entry : videos_by_rating.entrySet())
                list.add(entry.getKey());

        // Searches for first non-viewed item
        for (String title : list) {
            if (!history_list.contains(title)) {
                video = title;
                break;
            }
        }

        return filewriter.writeFile(action.getActionId(), "field",
                "BestRatedUnseenRecommendation result: " + video);
    }
/*
    public JSONObject Popular (Writer filewriter, ActionInputData action,
                                       Database database) throws IOException {

    }

    public JSONObject RecommendationFavorite (Writer filewriter, ActionInputData action,
                                       Database database) throws IOException {

    }
 */
    public JSONObject Search (Writer filewriter, ActionInputData action,
                                       Database database) throws IOException {
        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        Map<String, Double> videos_by_rating = new LinkedHashMap<>();
        List<String> history_list = new ArrayList<>();
        Map<String, Integer> history;

        // Gets User's history
        history = database.userMap.get(action.getUsername()).getHistory();

        // Creates a list with titles from history
        for (Map.Entry<String, Integer> entry : history.entrySet())
            history_list.add(entry.getKey());

        for (Movie movie : database.movieList) {
            if (movie.getGenres().contains(action.getGenre()))
                videos_by_rating.put(movie.getTitle(), movie.getRating());
        }
        for (Show show : database.showList) {
            if (show.getGenres().contains(action.getGenre()))
                videos_by_rating.put(show.getTitle(), show.getRating());
        }

        videos_by_rating = sortByKeyDouble(videos_by_rating, "asc");
        videos_by_rating = sortByValueDouble(videos_by_rating, "asc");

        // Creates list from Map
        for (Map.Entry<String, Double> entry : videos_by_rating.entrySet())
            list1.add(entry.getKey());

        // Removes seen videos from list
        for (String title : list1) {
            if (!history_list.contains(title))
                list2.add(title);
        }
        return filewriter.writeFile(action.getActionId(), "field",
                "SearchRecommendation result: " + list2);
    }
}
