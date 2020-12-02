package video;

import entertainment.Season;

import java.util.ArrayList;
import java.util.List;

public final class Show extends Video {

    private int numberofSeasons;

    private List<Season> seasons;

    public Show() {
    }

    public Show(final String title, final int year, final ArrayList<String> genres,
               final ArrayList<String> cast, final int numberofSeasons,
               final List<Season> seasons) {
        super(title, year, genres, cast);
        this.numberofSeasons = numberofSeasons;
        this.seasons = seasons;
    }

    public int getNumberofSeasons() {
        return numberofSeasons;
    }

    public void setNumberofSeasons(final int numberofSeasons) {
        this.numberofSeasons = numberofSeasons;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(final List<Season> seasons) {
        this.seasons = seasons;
    }

    /**
     * Returns average of ratings
     * @return
     */
    public double getRating() {
        double rating = 0.0;
            for (Season s : this.getSeasons()) {
                double seasonRating = 0.0;
                if (s.getRatings().size() != 0) {
                    for (double r : s.getRatings()) {
                        seasonRating += r;
                    }
                    seasonRating /= s.getRatings().size();
                    rating += seasonRating;
                }
            }
            rating /= this.getNumberofSeasons();
        return rating;
    }
}
