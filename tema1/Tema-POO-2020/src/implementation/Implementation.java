package implementation;

import fileio.*;
import org.jetbrains.annotations.NotNull;
import user.User;
import video.Movie;
import video.Show;

import java.util.ArrayList;
import java.util.List;

public class Implementation {
    public List<ActorInputData> actorList =  new ArrayList<>();
    public List<ActionInputData> actionList = new ArrayList<>();
    public List<Movie> movieList = new ArrayList<>();
    public List<Show> showList = new ArrayList<>();
    public List<User> userList = new ArrayList<>();

    public Implementation() {}

    public void populateActorList(Input input) {
        actorList.addAll(input.getActors());
    }

    public void populateActionList(Input input) {
        actionList.addAll(input.getCommands());
    }

    public void populateMovieList(Input input) {
        for(int i = 0; i < input.getMovies().size(); i++) {
            Movie movie = new Movie();
            MovieInputData input_movie = input.getMovies().get(i);

            movie.setYear(input_movie.getYear());
            movie.setCast(input_movie.getCast());
            movie.setTitle(input_movie.getTitle());
            movie.setGenres(input_movie.getGenres());
            movie.setDuration(input_movie.getDuration());

            movieList.add(i, movie);
        }
    }

    public void populateShowList(Input input) {
        for(int i = 0; i < input.getSerials().size(); i++) {
            Show show = new Show();
            SerialInputData input_show = input.getSerials().get(i);

            show.setYear(input_show.getYear());
            show.setCast(input_show.getCast());
            show.setTitle(input_show.getTitle());
            show.setGenres(input_show.getGenres());
            show.setNumberofSeasons(input_show.getNumberSeason());
            show.setSeasons(input_show.getSeasons());

            showList.add(i, show);
        }
    }

    public void populateUserList(Input input) {
        for(int i = 0; i < input.getUsers().size(); i++) {
            User user = new User();
            UserInputData input_user = input.getUsers().get(i);

            user.setUsername(input_user.getUsername());
            user.setSubscriptionType(input_user.getSubscriptionType());
            user.setHistory(input_user.getHistory());
            user.setFavoriteMovies(input_user.getFavoriteMovies());

            userList.add(i, user);
        }
    }
}
