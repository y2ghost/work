package study.ywork.web.controller;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import study.ywork.web.domain.CurrencyRate;

@Controller
@RequestMapping("/excel")
public class ExcelViewController {
    private static final String UTF8 = "utf-8";
    private final Logger log = LoggerFactory.getLogger(ExcelViewController.class);

    @GetMapping
    public String get(Model model) {
        model.addAttribute("todayCurrencyRates", getTodayForexRates());
        return "appExcelView";
    }

    private List<CurrencyRate> getTodayForexRates() {
        // 构造测试数据
        List<CurrencyRate> currencyRates = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();
        List<Currency> currencies = new ArrayList<>(Currency.getAvailableCurrencies());

        for (int i = 0; i < currencies.size(); i += 2) {
            String currencyPair = currencies.get(i) + "/" + currencies.get(i + 1);
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

    private void commonDownloadSettings(String fileName, HttpServletResponse response) {
        response.reset();
        response.setCharacterEncoding(UTF8);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        String encFileName = null;

        try {
            encFileName = URLEncoder.encode(fileName, UTF8);
        } catch (UnsupportedEncodingException ex) {
            encFileName = fileName;
        }

        String attachFileName = String.format("attachment;filename*=utf-8''%s", encFileName);
        response.setHeader("Content-Disposition", attachFileName);
    }

    // 示例下载文件写入数据到HttpServletResponse类
    public void writeFile(String fileName, String data, HttpServletResponse response) {
        commonDownloadSettings(fileName, response);
        try {
            ByteArrayInputStream dataIn = new ByteArrayInputStream(data.getBytes());
            OutputStream dataOut = response.getOutputStream();
            StreamUtils.copy(dataIn, dataOut);
            dataOut.close();
            dataIn.close();
        } catch (Exception ex) {
            log.error("下载文件失败: " + ex.getMessage());
            throw new RuntimeException("下载文件失败");
        }
    }
}
