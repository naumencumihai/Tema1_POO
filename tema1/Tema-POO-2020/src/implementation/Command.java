package implementation;

import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONObject;
import user.User;
import video.Movie;
import video.Show;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This class handles the functionalities of the command action
 */
public class Command {

    /**
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    // Favorite command
    public static JSONObject favorite(final Writer filewriter,
                                    final ActionInputData action,
                                    final Database database)
                                    throws IOException {
        User currentUser = database.userMap.get(action.getUsername());
        String video = action.getTitle();
        Map<String, Integer> history = currentUser.getHistory();

        if (currentUser.getFavoriteMovies().contains(video)) {
            return  filewriter.writeFile(action.getActionId(), "field",
                    "error -> " + video + " is already in favourite list");
        } else if (history.containsKey(video)) {
            currentUser.addFavoriteMovie(video);

            return filewriter.writeFile(action.getActionId(), "field",
                    "success -> " + video + " was added as favourite");
        } else {
            return filewriter.writeFile(action.getActionId(), "field",
                    "error -> " + video + " is not seen");
        }
    }

    /**
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    // View command
    public static JSONObject view(final Writer filewriter,
                                final ActionInputData action,
                                final Database database)
                                throws IOException {
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
        } else {
            history.put(video, 1);
        }
        currentUser.setHistory(history);
        return filewriter.writeFile(action.getActionId(),
                "field", "success -> " + video
                    + " was viewed with total views of " + history.get(video));
    }

    /**
     * Rating command
     * @param filewriter
     * @param action
     * @param database
     * @return
     * @throws IOException
     */
    public static JSONObject rating(final Writer filewriter,
                                    final ActionInputData action,
                                    final Database database)
                                    throws IOException {
        User currentUser = database.userMap.get(action.getUsername());
        String title = action.getTitle();
        // Checks if video is viewed by user
        if (currentUser.getHistory().containsKey(title)) {
            // Checks if video is already rated
            if (currentUser.getRatings() != null
                    && currentUser.getRatings().containsKey(title)
                    && (!database.showMap.containsKey(title))) {
                return filewriter.writeFile(action.getActionId(),
                        "field", "error -> " + title
                                + " has been already rated");
            } else {
                // Specified video is a Movie
                if (database.movieMap.containsKey(title)) {
                    currentUser.addRating(title, action.getGrade());
                    Movie movie = database.movieMap.get(title);

                    // Updates Movie's ratings
                    movie.addRating(action.getGrade());
                } else if (database.showMap.containsKey(title)) {
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
                        "field", "success -> " + title
                                + " was rated with " + action.getGrade()
                                + " by " + currentUser.getUsername());
            }
        }
        return filewriter.writeFile(action.getActionId(),
                "field", "error -> " + title
                        + " is not seen");
    }
}
