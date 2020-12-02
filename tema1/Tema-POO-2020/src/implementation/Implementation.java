package implementation;

import common.Constants;
import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONArray;
import java.io.IOException;

import static implementation.Command.favorite;
import static implementation.Command.view;
import static implementation.Command.rating;
import static implementation.Query.ratings;
import static implementation.Query.videoFavorite;
import static implementation.Query.longest;
import static implementation.Query.mostViewed;
import static implementation.Query.average;
import static implementation.Query.awards;
import static implementation.Query.filterDescription;
import static implementation.Recommendation.search;
import static implementation.Recommendation.bestUnseen;
import static implementation.Recommendation.standard;
import static implementation.Recommendation.popular;
import static implementation.Recommendation.recommendationFavorite;

public final class Implementation {

    /**
     * Implementation Method
     * @param filewriter
     * @param database
     * @param result
     * @throws IOException
     */

    public void implementaion(final Writer filewriter,
                              final  Database database,
                              final  JSONArray result)
            throws IOException {
        for (ActionInputData action : database.actionList) {
            switch (action.getActionType()) {
                case Constants.COMMAND:
                    switch (action.getType()) {
                        case "favorite" ->
                                result.add(favorite(
                                        filewriter, action, database));
                        case "view" ->
                                result.add(view(
                                        filewriter, action, database));
                        case "rating" ->
                                result.add(rating(
                                        filewriter, action, database));
                        default -> throw new IllegalStateException(
                                "Unexpected value: " + action.getType());
                    }
                    break;
                case Constants.QUERY:
                    switch (action.getObjectType()) {
                        case "users":
                            result.add(Query.numberofRatings(
                                    filewriter, action, database));
                            break;
                        case "movies":
                        case "shows":
                            switch (action.getCriteria()) {
                                case "ratings" ->
                                        result.add(ratings(
                                                filewriter, action, database));
                                case "favorite" ->
                                        result.add(videoFavorite(
                                                filewriter, action, database));
                                case "longest" ->
                                        result.add(longest(
                                                filewriter, action, database));
                                case "most_viewed" ->
                                        result.add(mostViewed(
                                                filewriter, action, database));
                                default -> throw new IllegalStateException(
                                        "Unexpected value: " + action.getCriteria());
                            }
                            break;
                        case "actors":
                            switch (action.getCriteria()) {
                                case "average" ->
                                        result.add(average(
                                                filewriter, action, database));
                                case "awards" ->
                                        result.add(awards(
                                                filewriter, action, database));
                                case "filter_description" ->
                                        result.add(filterDescription(
                                                filewriter, action, database));
                                default -> throw new IllegalStateException(
                                        "Unexpected value: " + action.getCriteria());
                            }
                            break;
                        default:
                            throw new IllegalStateException(
                                    "Unexpected value: " + action.getObjectType());
                    }
                    break;
                case Constants.RECOMMENDATION:
                    switch (action.getType()) {
                        case "standard" -> result.add(standard(
                                filewriter, action, database));

                        case "best_unseen" -> result.add(bestUnseen(
                                filewriter, action, database));
                        case "search" -> result.add(search(
                                filewriter, action, database));

                        case "popular" ->
                                result.add(popular(
                                        filewriter, action, database));
                        case "favorite" ->
                                result.add(recommendationFavorite(
                                        filewriter, action, database));
                        default -> throw new IllegalStateException(
                                "Unexpected value: " + action.getType());
                    }
                    break;
                default:
                    throw new IllegalStateException(
                            "Unexpected value: " + action.getActionType());
            }
        }
    }
}
