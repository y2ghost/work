package study.ywork.doc.util;

import study.ywork.doc.domain.Movie;

import java.util.Comparator;

public class MovieComparator implements Comparator<Movie> {
    public static final int BY_TITLE = 0;
    public static final int BY_YEAR = 1;
    private int type;

    public MovieComparator(int type) {
        this.type = type;
    }

    public int compare(Movie m1, Movie m2) {
        if (type == BY_YEAR) {
            int c = m1.getYear() - m2.getYear();
            if (c != 0)
                return c;
        }
        return m1.getTitle().compareTo(m2.getTitle());
    }
}
