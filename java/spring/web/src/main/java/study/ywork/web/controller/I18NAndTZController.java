package study.ywork.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.TimeZone;

/*
 * 演示i18N语言配置和时区处理的例子
 */
@Controller
@RequestMapping("tzandi18n")
public class I18NAndTZController {
    @GetMapping(value = "/language", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String handleLanguage(Locale locale) {
        return String.format("语言: %s, 国家: %s", locale.getLanguage(), locale.getDisplayCountry());
    }

    @RequestMapping(value = "/timezone", method = {RequestMethod.GET,
            RequestMethod.POST}, produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String handleTimeZone(Locale locale, ZoneId zoneId) {
        ZoneOffset serverZoneOffset = ZoneOffset.ofTotalSeconds(TimeZone.getDefault().getRawOffset() / 1000);
        return String.format("客户端时区: %s%n服务端时区: %s%n本地化语言: %s%n", zoneId.normalized().getId(), serverZoneOffset.getId(),
                locale);
    }

    @GetMapping("/timezone/custom")
    public String customTimezone() {
        return "time-zone";
    }

    @PostMapping(value = "/timezone/custom")
    public String handleTimeZoneCustorm(Locale locale, HttpServletRequest req, HttpServletResponse res,
                                        @RequestParam("timeZoneOffset") int timeZoneOffset) {
        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(-timeZoneOffset * 60);
        TimeZone timeZone = TimeZone.getTimeZone(zoneOffset);
        LocaleContextResolver localeResolver = (LocaleContextResolver) RequestContextUtils.getLocaleResolver(req);

        if (null != localeResolver) {
            localeResolver.setLocaleContext(req, res, new SimpleTimeZoneAwareLocaleContext(locale, timeZone));
        }

        return "forward:/tzandi18n/timezone";
    }
}
