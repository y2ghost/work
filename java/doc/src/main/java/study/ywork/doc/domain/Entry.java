package study.ywork.doc.domain;

import java.util.ArrayList;
import java.util.List;

public class Entry {
    private int year;
    private Movie movie;
    private Category category;
    private List<Screening> screenings = new ArrayList<>();

    public void addScreening(Screening screening) {
        screenings.add(screening);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
        if (movie.getEntry() == null) {
            movie.setEntry(this);
        }
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    
    public List<Screening> getScreenings() {
        return screenings;
    }
}
