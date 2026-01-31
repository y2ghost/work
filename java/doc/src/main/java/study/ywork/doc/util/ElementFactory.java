package study.ywork.doc.util;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.element.Text;
import study.ywork.doc.domain.Country;
import study.ywork.doc.domain.Director;
import study.ywork.doc.domain.Movie;

import java.util.ArrayList;
import java.util.List;

public class ElementFactory {
    public static com.itextpdf.layout.element.List getDirectorList(Movie movie) {
        com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List();
        for (Director director : movie.getDirectors()) {
            list.add(String.format("%s, %s", director.getName(), director.getGivenName()));
        }
        return list;
    }

    public static List<Text> getYearPhrase(Movie movie, PdfFont bold, PdfFont normal) {
        List<Text> year = new ArrayList<>();
        year.add(new Text("Year: ").setFont(bold));
        year.add(new Text(String.valueOf(movie.getYear())).setFont(normal));
        return year;
    }

    public static List<Text> getDurationPhrase(Movie movie, PdfFont bold, PdfFont normal) {
        List<Text> duration = new ArrayList<>();
        duration.add(new Text("Duration: ").setFont(bold));
        duration.add(new Text(String.valueOf(movie.getDuration())).setFont(normal));
        return duration;
    }

    public static com.itextpdf.layout.element.List getCountryList(Movie movie) {
        com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List();
        for (Country country : movie.getCountries()) {
            list.add(country.getName());
        }
        return list;
    }

    public static List<Text> getMovieTitlePhrase(Movie movie, PdfFont normal) {
        List<Text> list = new ArrayList<>();
        list.add(new Text(String.valueOf(movie.getTitle())).setFont(normal));
        return list;
    }

    public static List<Text> getOriginalTitlePhrase(Movie movie, PdfFont italic, PdfFont normal) {
        List<Text> list = new ArrayList<>();
        if (movie.getOriginalTitle() == null) {
            list.add(new Text("").setFont(normal));
        } else {
            list.add(new Text(movie.getOriginalTitle()).setFont(italic));
        }
        return list;
    }

    public static List<Text> getDirectorPhrase(Director director, PdfFont bold, PdfFont normal) {
        List<Text> list = new ArrayList<>();
        list.add(new Text(director.getName()).setFont(bold));
        list.add(new Text(", ").setFont(bold));
        list.add(new Text(director.getGivenName()).setFont(normal));
        return list;
    }

    public static List<Text> getCountryPhrase(Country country, PdfFont normal) {
        List<Text> list = new ArrayList<>();
        list.add(new Text(country.getName()).setFont(normal));
        return list;
    }

    private ElementFactory() {
    }
}
