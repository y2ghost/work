package study.ywork.web.converter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.yaml.snakeyaml.Yaml;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/*
 * 自定义YAML HttpMessageConverter对象示例，需要注册
 */
public class YamlConverter<T> extends AbstractHttpMessageConverter<T> {
    public YamlConverter() {
        super(new MediaType("text", "yaml", StandardCharsets.UTF_8));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException {
        Yaml yaml = new Yaml();
        return yaml.loadAs(inputMessage.getBody(), clazz);
    }

    @Override
    protected void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException {
        Yaml yaml = new Yaml();
        try (OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody())) {
            yaml.dump(t, writer);
        }
    }
}
