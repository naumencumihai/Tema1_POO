package utils;

import actor.ActorsAwards;
import common.Constants;
import entertainment.Genre;
import entertainment.Season;
import fileio.ActionInputData;
import fileio.ActorInputData;
import implementation.Database;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import video.Movie;
import video.Show;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Comparator;

/**
 * The class contains static methods that helps with parsing.
 *
 * We suggest you add your static methods here or in a similar class.
 */
public final class Utils {
    /**
     * for coding style
     */
    private Utils() {
    }

    /**
     * Transforms a string into an enum
     * @param genre of video
     * @return an Genre Enum
     */
    public static Genre stringToGenre(final String genre) {
        return switch (genre.toLowerCase()) {
            case "action" -> Genre.ACTION;
            case "adventure" -> Genre.ADVENTURE;
            case "drama" -> Genre.DRAMA;
            case "comedy" -> Genre.COMEDY;
            case "crime" -> Genre.CRIME;
            case "romance" -> Genre.ROMANCE;
            case "war" -> Genre.WAR;
            case "history" -> Genre.HISTORY;
            case "thriller" -> Genre.THRILLER;
            case "mystery" -> Genre.MYSTERY;
            case "family" -> Genre.FAMILY;
            case "horror" -> Genre.HORROR;
            case "fantasy" -> Genre.FANTASY;
            case "science fiction" -> Genre.SCIENCE_FICTION;
            case "action & adventure" -> Genre.ACTION_ADVENTURE;
            case "sci-fi & fantasy" -> Genre.SCI_FI_FANTASY;
            case "animation" -> Genre.ANIMATION;
            case "kids" -> Genre.KIDS;
            case "western" -> Genre.WESTERN;
            case "tv movie" -> Genre.TV_MOVIE;
            default -> null;
        };
    }

    /**
     * Transforms a string into an enum
     * @param award for actors
     * @return an ActorsAwards Enum
     */
    public static ActorsAwards stringToAwards(final String award) {
        return switch (award) {
            case "BEST_SCREENPLAY" -> ActorsAwards.BEST_SCREENPLAY;
            case "BEST_SUPPORTING_ACTOR" -> ActorsAwards.BEST_SUPPORTING_ACTOR;
            case "BEST_DIRECTOR" -> ActorsAwards.BEST_DIRECTOR;
            case "BEST_PERFORMANCE" -> ActorsAwards.BEST_PERFORMANCE;
            case "PEOPLE_CHOICE_AWARD" -> ActorsAwards.PEOPLE_CHOICE_AWARD;
            default -> null;
        };
    }

    /**
     * Transforms an array of JSON's into an array of strings
     * @param array of JSONs
     * @return a list of strings
     */
    public static ArrayList<String> convertJSONArray(final JSONArray array) {
        if (array != null) {
            ArrayList<String> finalArray = new ArrayList<>();
            for (Object object : array) {
                finalArray.add((String) object);
            }
            return finalArray;
        } else {
            return null;
        }
    }

    /**
     * Transforms an array of JSON's into a map
     * @param jsonActors array of JSONs
     * @return a map with ActorsAwardsa as key and Integer as value
     */
    public static Map<ActorsAwards, Integer> convertAwards(final JSONArray jsonActors) {
        Map<ActorsAwards, Integer> awards = new LinkedHashMap<>();

        for (Object iterator : jsonActors) {
            awards.put(stringToAwards((String) ((JSONObject) iterator).get(Constants.AWARD_TYPE)),
                    Integer.parseInt(((JSONObject) iterator).get(Constants.NUMBER_OF_AWARDS)
                            .toString()));
        }

        return awards;
    }

    /**
     * Transforms an array of JSON's into a map
     * @param movies array of JSONs
     * @return a map with String as key and Integer as value
     */
    public static Map<String, Integer> watchedMovie(final JSONArray movies) {
        Map<String, Integer> mapVideos = new LinkedHashMap<>();

        if (movies != null) {
            for (Object movie : movies) {
                mapVideos.put((String) ((JSONObject) movie).get(Constants.NAME),
                        Integer.parseInt(((JSONObject) movie).get(Constants.NUMBER_VIEWS)
                                .toString()));
            }
        } else {
            System.out.println("NU ESTE VIZIONAT NICIUN FILM");
        }

        return mapVideos;
    }

    /**
     * Filters movies by year and genre
     * @param videos
     * @param action
     * @return
     */
    public static List<Movie> filterMovies(final List<Movie> videos,
                                      final ActionInputData action) {
        List<Movie> filteredByYear = new ArrayList<>();
        List<Movie> filtered = new ArrayList<>();

        List<String> year = action.getFilters().get(0);
        List<String> genre = action.getFilters().get(1);

        if (year.get(0) != null) {
            for (Movie video : videos) {
                if (year.get(0).equals(Integer.toString(video.getYear()))) {
                    filteredByYear.add(video);
                }
            }
        } else {
            filteredByYear.addAll(videos);
        }

        if (genre.get(0) != null) {
            for (Movie video : filteredByYear) {
                if (video.getGenres().containsAll(genre)) {
                    filtered.add(video);
                }
            }
        } else {
            filtered.addAll(filteredByYear);
        }

        return filtered;
    }

    /**
     * Filters Shows by year and genre
     * @param videos
     * @param action
     * @return
     */
    public static List<Show> filterShows(final List<Show> videos,
                                    final ActionInputData action) {
        List<Show> filteredByYear = new ArrayList<>();
        List<Show> filtered = new ArrayList<>();

        List<String> year = action.getFilters().get(0);
        List<String> genre = action.getFilters().get(1);

        if (year.get(0) != null) {
            for (Show video : videos) {
                if (year.get(0).equals(Integer.toString(video.getYear()))) {
                    filteredByYear.add(video);
                }
            }
        } else {
            filteredByYear.addAll(videos);
        }

        if (genre != null) {
            for (Show video : filteredByYear) {
                if (video.getGenres().containsAll(genre)) {
                    filtered.add(video);
                }
            }
        } else {
            filtered.addAll(filteredByYear);
        }

        return filtered;
    }

    /**
     * Method to sort hashmap by values
     * @param map
     * @param order
     * @return
     */
    public static HashMap<String, Integer> sortByValueInteger(final Map<String, Integer> map,
                                                              final String order) {
        if (order.equals("asc")) {
            LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
            return sortedMap;
        } else if (order.equals("desc")) {
            LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
            return reverseSortedMap;
        }
        return (HashMap<String, Integer>) map;
    }

    /**
     * Method to sort hashmap by keys
     * @param map
     * @param order
     * @return
     */
    public static HashMap<String, Integer> sortByKeyInteger(final Map<String, Integer> map,
                                                            final String order) {
        if (order.equals("asc")) {
            LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
            return sortedMap;
        } else if (order.equals("desc")) {
            LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                    .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
            return reverseSortedMap;
        }
        return (HashMap<String, Integer>) map;
    }

    /**
     * Method to sort hashmap by values
     * @param map
     * @param order
     * @return
     */
    public static HashMap<String, Double> sortByValueDouble(final Map<String, Double> map,
                                                            final String order) {
        if (order.equals("asc")) {
            LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
            return sortedMap;

        } else if (order.equals("desc")) {
            LinkedHashMap<String, Double> reverseSortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
            return reverseSortedMap;
        }
        return (HashMap<String, Double>) map;
    }

    /**
     * Method to sort hashmap by keys
     * @param map
     * @param order
     * @return
     */
    public static HashMap<String, Double> sortByKeyDouble(final Map<String, Double> map,
                                                          final String order) {
        if (order.equals("asc")) {
            LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
            return sortedMap;

        } else if (order.equals("desc")) {
            LinkedHashMap<String, Double> reverseSortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                    .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
            return reverseSortedMap;
        }
        return (HashMap<String, Double>) map;
    }

    /**
     * Calculates average rating for an actor
     * @param database
     * @param actor
     * @return
     */
    public static Double calculateAverage(final Database database,
                                          final ActorInputData actor) {
        ArrayList<String> filmography = actor.getFilmography();
        List<Double> ratings;
        double movieAverage, showAverage, seasonAverage;
        double average = 0.0;
        int size = 0;
        for (String title : filmography) {
            movieAverage = 0.0;
            showAverage = 0.0;
            if (database.movieMap.get(title) != null) {
                ratings = database.movieMap.get(title).getRatings();
                for (double rating : ratings) {
                    movieAverage += rating;
                }
                if (movieAverage != 0.0) {
                    movieAverage /= ratings.size();
                    average += movieAverage;
                    size++;
                }
            } else if (database.showMap.get(title) != null) {
                for (Season season : database.showMap.get(title).getSeasons()) {
                    seasonAverage = 0;
                    ratings = season.getRatings();
                    for (double rating : ratings) {
                        seasonAverage += rating;
                    }
                    if (seasonAverage != 0.0) {
                        seasonAverage /= ratings.size();
                    }
                    showAverage += seasonAverage;
                }
                showAverage /= database.showMap.get(title).getNumberofSeasons();
                if (showAverage != 0.0) {
                    average += showAverage;
                    size++;
                }
            }
        }
        average /= size;
        return average;
    }
}
