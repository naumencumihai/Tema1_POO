package video;

import java.util.ArrayList;

public abstract class Video {

    private String title;

    private int year;

    private ArrayList<String> genres;

    private ArrayList<String> cast;


    public Video() {
    }

    public Video(final String title, final int year,
                 final ArrayList<String> genres, final ArrayList<String> cast) {
        this.title = title;
        this.year = year;
        this.genres = genres;
        this.cast = cast;
    }

    /**
     * Returns the Title
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the Title
     * @param title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Returns year
     * @return
     */
    public int getYear() {
        return year;
    }

    /**
     * Set year
     * @param year
     */
    public void setYear(final int year) {
        this.year = year;
    }

    /**
     * Returns list of Genres
     * @return
     */
    public ArrayList<String> getGenres() {
        return genres;
    }

    /**
     * Set genres list
     * @param genres
     */
    public void setGenres(final ArrayList<String> genres) {
        this.genres = genres;
    }

    /**
     * Returns list of cast
     * @return
     */
    public ArrayList<String> getCast() {
        return cast;
    }

    /**
     * Set cast list
     * @param cast
     */
    public void setCast(final ArrayList<String> cast) {
        this.cast = cast;
    }
}
