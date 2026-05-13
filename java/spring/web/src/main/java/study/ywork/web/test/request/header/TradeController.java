package study.ywork.web.test.request.header;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("trades")
public class TradeController {
    private static final String VIEW = "message";

    @GetMapping(headers = "User-Agent")
    public String handleAllTradesRequests(@RequestHeader("User-Agent") String userAgent, Model model) {
        String msg = "User-Agent: " + userAgent;
        System.out.println(msg);
        model.addAttribute("msg", msg);
        return VIEW;
    }

    @GetMapping(headers = "From")
    public String handleRequestByFromHeader(@RequestHeader("From") String from, Model model) {
        String msg = "From: " + from;
        System.out.println(msg);
        model.addAttribute("msg", msg);
        return VIEW;
    }

    @GetMapping
    public String handleRequestWithAllHeaders(@RequestHeader Map<String, String> header, Model model) {
        String msg = "Trade request with all headers : " + header;
        System.out.println(msg);
        model.addAttribute("msg", msg);
        return VIEW;
    }

    @GetMapping("tradeCurrencies")
    public String handleRequestWithAllHeaders2(@RequestHeader HttpHeaders httpHeaders, Model model) {
        String msg = "All Headers: " + httpHeaders;
        System.out.println(msg);
        model.addAttribute("msg", msg);
        return VIEW;
    }

    @GetMapping(headers = {"User-Agent", "Accept-Language"})
    public String handleRequestByTwoHeaders(@RequestHeader("User-Agent") String userAgent,
                                            @RequestHeader("Accept-Language") String acceptLanguage, Model map) {
        String msg = "User-Agent, Accept-Language: " + userAgent + ", " + acceptLanguage;
        System.out.println(msg);
        map.addAttribute("msg", msg);
        return VIEW;
    }

    @GetMapping(value = "{tradeId}")
    public String handleRequestById(@PathVariable("tradeId") String tradeId,
                                    @RequestHeader("If-Modified-Since") Date date, Model model) {
        String msg = "tradeId, If-Modified-Since: " + tradeId + ", " + date;
        System.out.println(msg);
        model.addAttribute("msg", msg);
        return VIEW;
    }

    @GetMapping(value = "exchangeRates")
    public String handleExchangeRatesRequest(@RequestHeader(value = "Accept", required = false) String acceptHeader,
                                             Model model) {
        String msg = "Accept: " + acceptHeader;
        System.out.println(msg);
        model.addAttribute("msg", msg);
        return VIEW;
    }
}
