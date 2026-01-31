package study.ywork.boot.test.mock;

import org.springframework.stereotype.Component;

@Component
public class NameProcessor {
    private NormalNameService normalNameService;
    private FormattedNameService formattedNameService;

    public NameProcessor(NormalNameService normalNameService,
                         FormattedNameService formattedNameService ) {
        this.normalNameService = normalNameService;
        this.formattedNameService = formattedNameService;
    }

    public String getNormalName() {
        return normalNameService.getName();
    }

    public String getFormattedName() {
        return formattedNameService.getFormattedName();
    }
}
