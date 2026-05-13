package study.ywork.web.test.response.strategy;

import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.ArrayList;
import java.util.List;

/*
 * 自定义内容协商策略
 */
public class MyStrategy implements ContentNegotiationStrategy {
    @Override
    public List<MediaType> resolveMediaTypes(NativeWebRequest nativeWebRequest) {
        List<MediaType> mediaTypes = new ArrayList<>();
        String acceptLang = nativeWebRequest.getHeader("Accept-Language");

        if (null == acceptLang || !acceptLang.toLowerCase().contains("en")) {
            mediaTypes.add(MediaType.APPLICATION_JSON);
        } else {
            mediaTypes.add(MediaType.TEXT_PLAIN);
        }

        System.out.printf("acceptLang=%s, mediaTypes=%s%n", acceptLang, mediaTypes);
        return mediaTypes;
    }
}
