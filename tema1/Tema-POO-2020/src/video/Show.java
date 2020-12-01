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
}
