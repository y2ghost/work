package study.ywork.doc.util;

import study.ywork.doc.domain.Category;
import study.ywork.doc.domain.Country;
import study.ywork.doc.domain.Director;
import study.ywork.doc.domain.Entry;
import study.ywork.doc.domain.Movie;
import study.ywork.doc.domain.Screening;
import study.ywork.doc.hsqldb.DatabaseConnection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqlUtils {
    public static final String MOVIES =
        "SELECT m.id, m.title, m.original_title, m.imdb, m.year, m.duration, "
            + "e.year, c.name, c.keyword, c.color "
            + "FROM film_movietitle m, festival_entry e, festival_category c "
            + "WHERE m.id = e.film_id AND e.category_id = c.id "
            + "ORDER BY m.title";

    public static final String DIRECTORS =
        "SELECT d.name, d.given_name "
            + "FROM film_director d, film_movie_director md "
            + "WHERE md.film_id = ? AND md.director_id = d.id";

    public static final String MOVIEDIRECTORS =
        "SELECT m.id, m.title, m.original_title, m.imdb, m.year, m.duration "
            + "FROM film_movietitle m, film_movie_director md "
            + "WHERE md.director_id = ? AND md.film_id = m.id "
            + "ORDER BY m.title";

    public static final String COUNTRIES =
        "SELECT c.country "
            + "FROM film_country c, film_movie_country mc "
            + "WHERE mc.film_id = ? AND mc.country_id = c.id";

    public static final String MOVIECOUNTRIES =
        "SELECT m.id, m.title, m.original_title, m.imdb, m.year, m.duration "
            + "FROM film_movietitle m, film_movie_country mc "
            + "WHERE mc.country_id = ? AND mc.film_id = m.id "
            + "ORDER BY m.title";

    public static final String DAYS =
        "SELECT DISTINCT day FROM festival_screening ORDER BY day";

    public static final String LOCATIONS =
        "SELECT DISTINCT location FROM festival_screening ORDER by location";

    public static final String SCREENINGS =
        "SELECT m.title, m.original_title, m.imdb, m.year, m.duration,"
            + "s.day, s.time, s.location, s.press, "
            + "e.year, c.name, c.keyword, c.color, m.id "
            + "FROM festival_screening s, film_movietitle m, "
            + "festival_entry e, festival_category c "
            + "WHERE day = ? AND s.film_id = m.id "
            + "AND m.id = e.film_id AND e.category_id = c.id";

    public static final String MOVIESCREENINGS =
        "SELECT s.day, s.time, s.location, s.press "
            + "FROM festival_screening s "
            + "WHERE s.film_id = ?";

    public static final String PRESS =
        "SELECT m.title, m.original_title, m.imdb, m.year, m.duration,"
            + "s.day, s.time, s.location, s.press, "
            + "e.year, c.name, c.keyword, c.color, m.id "
            + "FROM festival_screening s, film_movietitle m, "
            + "festival_entry e, festival_category c "
            + "WHERE s.press=1 AND s.film_id = m.id "
            + "AND m.id = e.film_id AND e.category_id = c.id "
            + "ORDER BY day, time ASC";

    public static Movie getMovie(ResultSet rs)
        throws SQLException {
        Movie movie = new Movie();
        movie.setTitle(rs.getString("title"));
        if (rs.getObject("original_title") != null) {
            movie.setOriginalTitle(
                rs.getString("original_title"));
        }
        movie.setImdb(rs.getString("imdb"));
        movie.setYear(rs.getInt("year"));
        movie.setDuration(rs.getInt("duration"));
        return movie;
    }

    public static Director getDirector(ResultSet rs)
        throws SQLException {
        Director director = new Director();
        director.setName(rs.getString("name"));
        director.setGivenName(rs.getString("given_name"));
        return director;
    }

    public static Country getCountry(ResultSet rs) throws SQLException {
        Country country = new Country();
        country.setName(rs.getString("country"));
        return country;
    }

    public static Entry getEntry(ResultSet rs) throws SQLException {
        Entry entry = new Entry();
        entry.setYear(rs.getInt("year"));
        return entry;
    }

    public static Category getCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setName(rs.getString("name"));
        category.setKeyword(rs.getString("keyword"));
        category.setColor(rs.getString("color"));
        return category;
    }

    public static Screening getScreening(ResultSet rs) throws SQLException {
        Screening screening = new Screening();
        screening.setDate(rs.getDate("day"));
        screening.setTime(rs.getTime("time"));
        screening.setLocation(rs.getString("location"));
        screening.setPress(rs.getInt("press") == 1);
        return screening;
    }

    public static List<Screening> getScreenings(Date day)
        throws SQLException {
        List<Screening> list = new ArrayList<>();
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        try (PreparedStatement stm = connection.createPreparedStatement(SCREENINGS)) {
            stm.setDate(1, day);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                Screening screening = getScreening(rs);
                Movie movie = getMovie(rs);

                for (Director director :
                    getDirectors(connection, rs.getInt("id"))) {
                    movie.addDirector(director);
                }

                for (Country country :
                    getCountries(connection, rs.getInt("id"))) {
                    movie.addCountry(country);
                }

                Entry entry = getEntry(rs);
                Category category = getCategory(rs);
                entry.setCategory(category);
                entry.setMovie(movie);
                movie.setEntry(entry);
                screening.setMovie(movie);
                list.add(screening);
            }
        }

        return list;
    }

    public static List<Screening> getScreenings(DatabaseConnection connection, int filmId)
        throws SQLException {
        List<Screening> list = new ArrayList<>();
        try (PreparedStatement stm = connection.createPreparedStatement(MOVIESCREENINGS)) {
            stm.setInt(1, filmId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                list.add(getScreening(rs));
            }
        }

        return list;
    }

    public static List<Screening> getPressPreviews()
        throws SQLException {
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        List<Screening> list = new ArrayList<>();

        try (Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery(PRESS);
            while (rs.next()) {
                Screening screening = getScreening(rs);
                Movie movie = getMovie(rs);

                for (Director director : getDirectors(connection, rs.getInt("id"))) {
                    movie.addDirector(director);
                }

                for (Country country : getCountries(connection, rs.getInt("id"))) {
                    movie.addCountry(country);
                }

                Entry entry = getEntry(rs);
                Category category = getCategory(rs);
                entry.setCategory(category);
                entry.setMovie(movie);
                movie.setEntry(entry);
                screening.setMovie(movie);
                list.add(screening);
            }
        }

        return list;
    }

    public static List<Movie> getMovies()
        throws SQLException {
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        List<Movie> list = new ArrayList<>();

        try (Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery(MOVIES);
            while (rs.next()) {
                Movie movie = getMovie(rs);
                Entry entry = getEntry(rs);
                Category category = getCategory(rs);
                entry.setCategory(category);

                for (Screening screening : getScreenings(connection, rs.getInt("id"))) {
                    entry.addScreening(screening);
                }

                movie.setEntry(entry);
                for (Director director : getDirectors(connection, rs.getInt("id"))) {
                    movie.addDirector(director);
                }

                for (Country country : getCountries(connection, rs.getInt("id"))) {
                    movie.addCountry(country);
                }

                list.add(movie);
            }
        }

        return list;
    }

    public static List<Director> getDirectors(DatabaseConnection connection, int movieId)
        throws SQLException {
        List<Director> list = new ArrayList<>();
        try (PreparedStatement directors = connection.createPreparedStatement(DIRECTORS)) {
            directors.setInt(1, movieId);
            ResultSet rs = directors.executeQuery();

            while (rs.next()) {
                list.add(getDirector(rs));
            }
        }

        return list;
    }

    public static List<Country> getCountries(DatabaseConnection connection, int movieId)
        throws SQLException {
        List<Country> list = new ArrayList<>();
        try (PreparedStatement countries = connection.createPreparedStatement(COUNTRIES)) {
            countries.setInt(1, movieId);
            ResultSet rs = countries.executeQuery();

            while (rs.next()) {
                list.add(getCountry(rs));
            }
        }

        return list;
    }

    public static List<Movie> getMovies(DatabaseConnection connection, int directorId)
        throws SQLException {
        List<Movie> list = new ArrayList<>();
        try (PreparedStatement movies = connection.createPreparedStatement(MOVIEDIRECTORS)) {
            movies.setInt(1, directorId);
            ResultSet rs = movies.executeQuery();

            while (rs.next()) {
                Movie movie = getMovie(rs);
                for (Country country : getCountries(connection, rs.getInt("id"))) {
                    movie.addCountry(country);
                }

                list.add(movie);
            }
        }

        return list;
    }

    public static List<Movie> getMovies(DatabaseConnection connection, String countryId)
        throws SQLException {
        List<Movie> list = new ArrayList<>();
        try (PreparedStatement movies = connection.createPreparedStatement(MOVIECOUNTRIES)) {
            movies.setString(1, countryId);
            ResultSet rs = movies.executeQuery();

            while (rs.next()) {
                Movie movie = getMovie(rs);
                for (Director director : getDirectors(connection, rs.getInt("id"))) {
                    movie.addDirector(director);
                }

                list.add(movie);
            }
        }

        return list;
    }

    public static List<Date> getDays()
        throws SQLException {
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        List<Date> list = new ArrayList<>();

        try (Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery(DAYS);
            while (rs.next()) {
                list.add(rs.getDate("day"));
            }
        }

        return list;
    }

    public static List<String> getLocations()
        throws SQLException {
        DatabaseConnection connection = DBUtils.getFilmDBConnection();
        List<String> list = new ArrayList<>();

        try (Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery(LOCATIONS);
            while (rs.next()) {
                list.add(rs.getString("location"));
            }
        }

        return list;
    }

    private SqlUtils() {
    }
}
