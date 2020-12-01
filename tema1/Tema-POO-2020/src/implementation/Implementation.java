package implementation;

import common.Constants;
import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import user.User;
import video.Movie;
import video.Show;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Implementation extends Query{

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
            if (action.getActionType().equals((Constants.QUERY))) {
                if (action.getObjectType().equals("users"))
                    result.add(this.NumberofRatings(filewriter, action, database));
                else if (action.getObjectType().equals("movies")) {

                }
                else if (action.getObjectType().equals("shows")) {

                }
                else if (action.getObjectType().equals("actors")) {
                    if (action.getCriteria().equals("average")) {
                        result.add(this.Average(filewriter, action, database));
                    }
                    else if (action.getCriteria().equals("awards")) {
                        result.add(this.Awards(filewriter, action, database));
                    }
                }
            }

        }
    }

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
}
