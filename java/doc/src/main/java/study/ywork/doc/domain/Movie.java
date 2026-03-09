package study.ywork.doc.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Movie implements Comparable<Movie> {
    private String title;
    private String originalTitle = null;
    private String imdb;
    private int year;
    private int duration;
    private final List<Director> directors = new ArrayList<>();
    private final List<Country> countries = new ArrayList<>();
    private Entry entry = null;

    public void addDirector(Director director) {
        directors.add(director);
    }

    public void addCountry(Country country) {
        countries.add(country);
    }

    public String getMovieTitle() {
        if (title.endsWith(", A")) {
            return "A " + title.substring(0, title.length() - 3);
        }
        if (title.endsWith(", The")) {
            return "The " + title.substring(0, title.length() - 5);
        }
        return title;
    }

    public String getMovieTitle(boolean prefix) {
        if (title.endsWith(", A")) {
            if (prefix) {
                return "A ";
            } else {
                return title.substring(0, title.length() - 3);
            }
        }

        if (title.endsWith(", The")) {
            if (prefix) {
                return "The ";
            } else {
                return title.substring(0, title.length() - 5);
            }
        }

        if (prefix) {
            return null;
        } else {
            return title;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getImdb() {
        return imdb;
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<Director> getDirectors() {
        return directors;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
        if (entry.getMovie() == null) {
            entry.setMovie(this);
        }
    }

    public int compareTo(Movie o) {
        return title.compareTo(o.title);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Movie m) {
            return Objects.equals(title, m.title);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
