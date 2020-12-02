package implementation;

import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONObject;
import video.Movie;
import video.Show;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static utils.Utils.sortByKeyDouble;
import static utils.Utils.sortByValueDouble;


public class Recommendation {

    /**
     * Returns one recommendation
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    public static JSONObject standard(final Writer filewriter,
                                      final ActionInputData action,
                                      final Database database)
            throws IOException {
        String video = "All videos watched";
        List<String> list = new ArrayList<>();
        List<String> historyList = new ArrayList<>();
        Map<String, Integer> history;

        for (Movie movie : database.movieList) {
            list.add(movie.getTitle());
        }
        for (Show show : database.showList) {
            list.add(show.getTitle());
        }

        // Gets User's history
        history = database.userMap.get(action.getUsername()).getHistory();

        // Creates a list with titles from history
        for (Map.Entry<String, Integer> entry : history.entrySet()) {
            historyList.add(entry.getKey());
        }

        // Searches for first non-viewed item
        for (String title : list) {
            if (!historyList.contains(title)) {
                video = title;
                break;
            }
        }

        return filewriter.writeFile(action.getActionId(), "field",
                "StandardRecommendation result: " + video);
    }

    /**
     * Return the best unseen video
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    public static JSONObject bestUnseen(final Writer filewriter,
                                        final ActionInputData action,
                                        final Database database)
            throws IOException {
        String video = "All videos watched";
        List<String> list = new ArrayList<>();
        List<String> historyList = new ArrayList<>();
        Map<String, Integer> history;
        Map<String, Double> videosByRating = new LinkedHashMap<>();

        // Gets User's history
        history = database.userMap.get(action.getUsername()).getHistory();

        // Creates a list with titles from history
        for (Map.Entry<String, Integer> entry : history.entrySet()) {
            historyList.add(entry.getKey());
        }

        // Populates videosByRating Map
        for (Movie movie : database.movieList) {
            videosByRating.put(movie.getTitle(), movie.getRating());
        }

        for (Show show : database.showList) {
            videosByRating.put(show.getTitle(), show.getRating());
        }

        // Sorts by ratings in descending order
        videosByRating = sortByValueDouble(videosByRating, "desc");

        // Creates list from Map
        for (Map.Entry<String, Double> entry : videosByRating.entrySet()) {
            list.add(entry.getKey());
        }

        // Searches for first non-viewed item
        for (String title : list) {
            if (!historyList.contains(title)) {
                video = title;
                break;
            }
        }

        return filewriter.writeFile(action.getActionId(), "field",
                "BestRatedUnseenRecommendation result: " + video);
    }

    /**
     * TODO
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    public static JSONObject popular(final Writer filewriter,
                                      final ActionInputData action,
                                      final  Database database)
            throws IOException {
        return filewriter.writeFile(action.getActionId(), "field",
                "TODO ");
    }

    /**
     * TODO
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    public static  JSONObject recommendationFavorite(final Writer filewriter,
                                                      final  ActionInputData action,
                                                      final  Database database)
            throws IOException {
        return filewriter.writeFile(action.getActionId(), "field",
                "TODO ");
    }

    /**
     * Return list of recommendations
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    public static JSONObject search(final Writer filewriter,
                                    final ActionInputData action,
                                    final Database database)
            throws IOException {
        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        Map<String, Double> videosByRating = new LinkedHashMap<>();
        List<String> historyList = new ArrayList<>();
        Map<String, Integer> history;

        // Gets User's history
        history = database.userMap.get(action.getUsername()).getHistory();

        // Creates a list with titles from history
        for (Map.Entry<String, Integer> entry : history.entrySet()) {
            historyList.add(entry.getKey());
        }

        for (Movie movie : database.movieList) {
            if (movie.getGenres().contains(action.getGenre())) {
                videosByRating.put(movie.getTitle(), movie.getRating());
            }
        }
        for (Show show : database.showList) {
            if (show.getGenres().contains(action.getGenre())) {
                videosByRating.put(show.getTitle(), show.getRating());
            }
        }

        videosByRating = sortByKeyDouble(videosByRating, "asc");
        videosByRating = sortByValueDouble(videosByRating, "asc");

        // Creates list from Map
        for (Map.Entry<String, Double> entry : videosByRating.entrySet()) {
            list1.add(entry.getKey());
        }

        // Removes seen videos from list
        for (String title : list1) {
            if (!historyList.contains(title)) {
                list2.add(title);
            }
        }
        return filewriter.writeFile(action.getActionId(), "field",
                "SearchRecommendation result: " + list2);
    }
}
