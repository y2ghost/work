package study.ywork.boot.formatter;

import org.springframework.format.Formatter;
import study.ywork.boot.domain.TradeAmount;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TradeAmountFormatter implements Formatter<TradeAmount> {
    private static final Pattern amountPattern = Pattern.compile("(\\d+)(\\w+)");

    @Override
    public TradeAmount parse(String text, Locale locale) throws ParseException {
        TradeAmount ta = new TradeAmount();
        Matcher matcher = amountPattern.matcher(text);

        if (matcher.find()) {
            try {
                ta.setAmount(new BigDecimal(Integer.parseInt(matcher.group(1))));
                ta.setCurrency(Currency.getInstance(matcher.group(2)));
            } catch (Exception e) {
                System.err.println(e);
            }
        }

        return ta;
    }

    @Override
    public String print(TradeAmount tradeAmount, Locale locale) {
        return tradeAmount.getAmount().toPlainString() + tradeAmount.getCurrency().getSymbol(locale);
    }
}
