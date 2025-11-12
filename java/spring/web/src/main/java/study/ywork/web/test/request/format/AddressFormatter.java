package study.ywork.web.test.request.format;

import org.springframework.format.Formatter;

import java.util.Locale;

public class AddressFormatter implements Formatter<Address> {
    private Style style = Style.FULL;

    public void setStyle(Style style) {
        this.style = style;
    }

    @Override
    public Address parse(String text, Locale locale) {
        if (null != text) {
            String[] parts = text.split(",");
            if (style == Style.FULL && parts.length == 4) {
                Address address = new Address();
                address.setStreet(parts[0].trim());
                address.setCity(parts[1].trim());
                address.setZipCode(parts[2].trim());
                address.setCounty(parts[3].trim());
                return address;
            } else if (style == Style.REGION && parts.length == 3) {
                Address address = new Address();
                address.setCity(parts[0].trim());
                address.setZipCode(parts[1].trim());
                address.setCounty(parts[2].trim());
                return address;
            }
        }

        return null;
    }

    @Override
    public String print(Address a, Locale l) {
        if (null == a) {
            return "";
        }

        if (Style.FULL == style) {
            return String.format(l, "%s, %s, %s, %s", a.getStreet(), a.getCity(), a.getZipCode(), a.getCounty());
        } else if (Style.REGION == style) {
            return String.format(l, "%s, %s, %s", a.getCity(), a.getZipCode(), a.getCounty());
        }

        return a.toString();
    }

    public enum Style {
        FULL, REGION
    }
}
