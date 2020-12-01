package implementation;

import actor.ActorsAwards;
import entertainment.Season;
import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.Writer;
import org.json.simple.JSONObject;
import user.User;

import java.io.IOException;
import java.util.*;

import static utils.Utils.stringToAwards;

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

        users_by_rating = sortByValueInteger(users_by_rating, action.getSortType());

        int count = 0;

        for (Map.Entry<String, Integer> entry : users_by_rating.entrySet()) {
            if (count == number - 1) break;
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
            if (entry.getValue() != 0) {
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

    private Double calculateAverage (Database database, ActorInputData actor) {
        ArrayList<String> filmography = actor.getFilmography();
        List<Double> ratings;
        double movie_average, show_average, season_average;
        double average = 0.0;
        int size = 0;
        for (String title : filmography) {
            movie_average = 0.0;
            show_average = 0.0;
            if (database.movieMap.get(title) != null) {
                ratings = database.movieMap.get(title).getRatings();
                for (double rating : ratings) {
                    movie_average += rating;
                }
                if (movie_average != 0.0) {
                    movie_average /= ratings.size();
                    average += movie_average;
                    size++;
                }
            }
            else if (database.showMap.get(title) != null){
                for (Season season : database.showMap.get(title).getSeasons()) {
                    season_average = 0;
                    ratings = season.getRatings();
                    for (double rating : ratings) {
                        season_average += rating;
                    }
                    if (season_average != 0.0) {
                        season_average /= ratings.size();
                    }
                    show_average += season_average;
                }
                show_average /= database.showMap.get(title).getNumberofSeasons();
                if (show_average != 0.0) {
                    average += show_average;
                    size++;
                }
            }
        }
        average /= size;
        return average;
    }

    // Function to sort hashmap by values
    private HashMap<String, Integer> sortByValueInteger(Map<String, Integer> map, String order) {
        if (order.equals("asc")) {
            LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
            return sortedMap;
        }
        else if (order.equals("desc")) {
            LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
            return reverseSortedMap;
        }
        return (HashMap<String, Integer>) map;
    }

    private HashMap<String, Integer> sortByKeyInteger(Map<String, Integer> map, String order) {
        if (order.equals("asc")) {
            LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
            return sortedMap;
        }
        else if (order.equals("desc")) {
            LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                    .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
            return reverseSortedMap;
        }
        return (HashMap<String, Integer>) map;
    }

    private HashMap<String, Double> sortByValueDouble(Map<String, Double> map, String order) {
        if (order.equals("asc")) {
            LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
            return sortedMap;
        }
        else if (order.equals("desc")) {
            LinkedHashMap<String, Double> reverseSortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
            return reverseSortedMap;
        }
        return (HashMap<String, Double>) map;
    }

    private HashMap<String, Double> sortByKeyDouble(Map<String, Double> map, String order) {
        if (order.equals("asc")) {
            LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
            return sortedMap;
        }
        else if (order.equals("desc")) {
            LinkedHashMap<String, Double> reverseSortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                    .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
            return reverseSortedMap;
        }
        return (HashMap<String, Double>) map;
    }
}
