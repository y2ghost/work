package study.ywork.web.test.request.bind;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("trades")
public class YyTradeController {
    @GetMapping("/{trade}")
    public String handleTradeRequest(YyTrade trade, Model model) {
        System.out.println(trade);
        if (trade.getTradeId() == 0) {
            model.addAttribute("msg", "没有找到对应的YyTrade");
        }

        return "message";
    }
}
