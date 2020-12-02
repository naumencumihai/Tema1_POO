package implementation;

import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.Input;
import fileio.MovieInputData;
import fileio.UserInputData;
import fileio.SerialInputData;
import user.User;
import video.Movie;
import video.Show;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A database class that contains Lists for
 * Actors, Actions, Users and Videos (Movies and TV Shows)
 */
public final class Database {
        public List<ActorInputData> actorList;
        public List<ActionInputData> actionList;
        public List<Movie> movieList;
        public List<Show> showList;
        public List<User> userList;

        public Map<String, ActorInputData> actorMap;
        public Map<String, Movie> movieMap;
        public Map<String, Show> showMap;
        public Map<String, User> userMap;

        public Database() {
            actorList = new ArrayList<>();
            actionList = new ArrayList<>();
            movieList = new ArrayList<>();
            showList = new ArrayList<>();
            userList = new ArrayList<>();

            actorMap = new HashMap<>();
            movieMap = new HashMap<>();
            showMap = new HashMap<>();
            userMap = new HashMap<>();
        }

    /**
     * Populates lists
     * @param input
     */
    public void populateLists(final Input input) {
        // Populates actors list
        actorList.addAll(input.getActors());
        // Populates actions list
        actionList.addAll(input.getCommands());
        // Populates movies list
        for (int i = 0; i < input.getMovies().size(); i++) {
            Movie movie = new Movie();
            MovieInputData inputMovie = input.getMovies().get(i);

            movie.setYear(inputMovie.getYear());
            movie.setCast(inputMovie.getCast());
            movie.setTitle(inputMovie.getTitle());
            movie.setGenres(inputMovie.getGenres());
            movie.setDuration(inputMovie.getDuration());

            movieList.add(i, movie);
        }
        // Populates shows list
        for (int i = 0; i < input.getSerials().size(); i++) {
            Show show = new Show();
            SerialInputData inputShow = input.getSerials().get(i);

            show.setYear(inputShow.getYear());
            show.setCast(inputShow.getCast());
            show.setTitle(inputShow.getTitle());
            show.setGenres(inputShow.getGenres());
            show.setNumberofSeasons(inputShow.getNumberSeason());
            show.setSeasons(inputShow.getSeasons());

            showList.add(i, show);
        }
        // Populates users list
        for (int i = 0; i < input.getUsers().size(); i++) {
            User user = new User();
            UserInputData inputUser = input.getUsers().get(i);

            user.setUsername(inputUser.getUsername());
            user.setSubscriptionType(inputUser.getSubscriptionType());
            user.setHistory(inputUser.getHistory());
            user.setFavoriteMovies(inputUser.getFavoriteMovies());

            userList.add(i, user);
        }
    }

    /**
     * Creates Maps
     */
    public void createMaps() {
            for (ActorInputData actor : actorList) {
                actorMap.put(actor.getName(), actor);
            }
            for (Movie movie : movieList) {
                movieMap.put(movie.getTitle(), movie);
            }
            for (Show show : showList) {
                showMap.put(show.getTitle(), show);
            }
            for (User user : userList) {
                userMap.put(user.getUsername(), user);
            }
    }
}
