package video;

import entertainment.Season;

import java.util.ArrayList;
import java.util.List;

public class Show extends Video{

    private int numberofSeasons;

    private List<Season> seasons;

    public Show() {
    }

    public Show(String title, int year, ArrayList<String> genres, ArrayList<String> cast, int numberofSeasons, List<Season> seasons) {
        super(title, year, genres, cast);
        this.numberofSeasons = numberofSeasons;
        this.seasons = seasons;
    }

    public int getNumberofSeasons() {
        return numberofSeasons;
    }

    public void setNumberofSeasons(int numberofSeasons) {
        this.numberofSeasons = numberofSeasons;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    public double getRating () {
        double rating = 0.0;
            for (Season s : this.getSeasons()) {
                double season_rating = 0.0;
                if (s.getRatings().size() != 0) {
                    for (double r : s.getRatings()) {
                        season_rating += r;
                    }
                    season_rating /= s.getRatings().size();
                    rating += season_rating;
                }
            }
            rating /= this.getNumberofSeasons();
        return rating;
    }
}
