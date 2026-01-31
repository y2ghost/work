package study.ywork.web.test.request.bind;

import org.springframework.core.convert.converter.Converter;

public class YyTradeConverter implements Converter<String, YyTrade> {
    private final YyTradeService tradeService;

    public YyTradeConverter(YyTradeService tradeService) {
        this.tradeService = tradeService;
    }

    @Override
    public YyTrade convert(String id) {
        try {
            Long tradeId = Long.valueOf(id);
            return tradeService.getTradeById(tradeId);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
