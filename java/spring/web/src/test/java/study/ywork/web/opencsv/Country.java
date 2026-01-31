package study.ywork.web.opencsv;

import com.opencsv.bean.CsvBindByName;

public class Country {
    @CsvBindByName(column = "id")
    private String id;

    @CsvBindByName(column = "country_code")
    private String countryCode;

    public Country(String id, String countryCode) {
        this.id = id;
        this.countryCode = countryCode;
    }
}
