package study.ywork.boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import study.ywork.boot.domain.TradeAmount;

/*
 * 参数转换的Formatter类示例
 * 测试方法: http://localhost:8080/trade?amount=100CNY
 */
@Controller
@RequestMapping("/trade")
public class TradeController {
    @GetMapping(produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String handleRequest(@RequestParam TradeAmount amount) {
        return "交易信息: " + amount;
    }
}
