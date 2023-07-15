package study.ywork.web.converter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import study.ywork.web.domain.Report;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*
 * 自定义HttpMessageConverter对象示例，需要注册
 */
public class ReportConverter extends AbstractHttpMessageConverter<Report> {
    public ReportConverter() {
        super(new MediaType("text", "report", StandardCharsets.UTF_8));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Report.class.isAssignableFrom(clazz);
    }

    @Override
    protected Report readInternal(Class<? extends Report> clazz, HttpInputMessage inputMessage) throws IOException {
        String requestBody = toString(inputMessage.getBody());
        int i = requestBody.indexOf("\n");

        if (i == -1) {
            throw new HttpMessageNotReadableException("第一行未发现", inputMessage);
        }

        String reportName = requestBody.substring(0, i).trim();
        String content = requestBody.substring(i).trim();
        Report report = new Report();
        report.setReportName(reportName);
        report.setContent(content);
        return report;
    }

    @Override
    protected void writeInternal(Report report, HttpOutputMessage outputMessage) throws IOException {
        try (OutputStream outputStream = outputMessage.getBody()) {
            String body = report.getReportName() + "\n" + report.getContent();
            outputStream.write(body.getBytes());
        }
    }

    private static String toString(InputStream inputStream) {
        String result = null;
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            result = scanner.useDelimiter("\\A").next();
        }

        return result;
    }
}
