package study.ywork.springboot.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import java.io.IOException;
import java.nio.charset.Charset;

public class ToDoErrorHandler extends DefaultResponseErrorHandler {
    private final static Logger LOG = LoggerFactory.getLogger(ToDoErrorHandler.class);
    private final static Charset DEFAULT_CHARSET = Charset.defaultCharset();

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String statusCode = response.getStatusCode().toString();
        String body = StreamUtils.copyToString(response.getBody(), DEFAULT_CHARSET);
        LOG.error(statusCode);
        LOG.error(body);
    }
}