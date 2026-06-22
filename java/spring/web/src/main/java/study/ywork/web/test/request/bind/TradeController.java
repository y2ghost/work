package study.ywork.web.test.request.bind;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("trades")
public class TradeController {
    private static final String VIEW = "message";
    private static final String FORMAT = "买卖: %s, 买入货币: %s, 卖出货币: %s";

    @GetMapping
    public String handleTradeRequest(Trade trade, Model model) {
        String msg = String.format(FORMAT, trade.getBuySell(), trade.getBuyCurrency(), trade.getSellCurrency());
        System.out.println(msg);
        model.addAttribute("msg", msg);
        return VIEW;
    }

    @GetMapping("{buySell}/{buyCurrency}/{sellCurrency}")
    public String handleTradeRequest2(Trade trade, Model model) {
        return handleTradeRequest(trade, model);
    }

    @GetMapping("paramTest")
    public String handleTradeRequest(@RequestParam("buySell") String buySell,
        @RequestParam("buyCurrency") String buyCurrency, @RequestParam("sellCurrency") String sellCurrency, Model map) {
        String msg = String.format(FORMAT, buySell, buyCurrency, sellCurrency);
        System.out.println(msg);
        map.addAttribute("msg", msg);
        return VIEW;
    }

    @GetMapping("pathTest/{buySell}/{buyCurrency}/{sellCurrency}")
    public String handleTradeRequest3(@PathVariable("buySell") String buySell,
        @PathVariable("buyCurrency") String buyCurrency, @PathVariable("sellCurrency") String sellCurrency, Model map) {
        return handleTradeRequest(buySell, buyCurrency, sellCurrency, map);
    }
}
