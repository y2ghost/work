package study.ywork.doc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import study.ywork.doc.domain.CurrencyRate;
import study.ywork.doc.view.ExcelView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Controller
@RequestMapping("/excel")
public class ExcelViewController {
    private final ExcelView excelView;

    public ExcelViewController(ExcelView excelView) {
        this.excelView = excelView;
    }

    @GetMapping
    public View get(Model model) {
        model.addAttribute("todayCurrencyRates", getTodayForexRates());
        return excelView;
    }

    private List<CurrencyRate> getTodayForexRates() {
        // 构造测试数据
        List<CurrencyRate> currencyRates = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();
        List<Currency> currencies = new ArrayList<>(Currency.getAvailableCurrencies());

        for (Currency currency : currencies) {
            String currencyPair = currency.getCurrencyCode() + "/" + currency.getDefaultFractionDigits();
            CurrencyRate cr = new CurrencyRate();
            cr.setCurrencyPair(currencyPair);
            cr.setDateTime(today);
            BigDecimal bidPrice = BigDecimal.valueOf(Math.random() * 5 + 1);
            bidPrice = bidPrice.setScale(3, RoundingMode.CEILING);
            cr.setBidPrice(bidPrice);
            BigDecimal askPrice = BigDecimal.valueOf(bidPrice.doubleValue() + Math.random() * 2 + 0.5);
            askPrice = askPrice.setScale(3, RoundingMode.CEILING);
            cr.setAskPrice(askPrice);
            currencyRates.add(cr);
        }

        return currencyRates;
    }
}
