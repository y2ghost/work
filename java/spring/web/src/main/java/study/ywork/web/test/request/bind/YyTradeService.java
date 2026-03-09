package study.ywork.web.test.request.bind;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class YyTradeService {
    private final HashMap<Long, YyTrade> trades = new HashMap<>();

    @PostConstruct
    private void postConstruct() {
        List<Currency> ccy = new ArrayList<>(Currency.getAvailableCurrencies());
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 1; i <= 10; i++) {
            YyTrade trade = new YyTrade();
            trade.setTradeId(i);
            trade.setBuySell(Math.random() > 0.5 ? "Buy" : "Sell");
            trade.setBuyCurrency(ccy.get(rnd.nextInt(0, ccy.size())).getCurrencyCode());
            trade.setSellCurrency(ccy.get(rnd.nextInt(0, ccy.size())).getCurrencyCode());
            trades.put((long) i, trade);
        }
    }

    public YyTrade getTradeById(Long id) {
        return trades.get(id);

    }

    public List<YyTrade> getAllTrades() {
        return new ArrayList<>(trades.values());
    }
}
