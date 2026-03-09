package study.ywork.web.converter;

import com.opencsv.CSVReader;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import study.ywork.web.domain.ListParam;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

/*
 * 自定义CSV HttpMessageConverter对象示例，需要注册
 */
public class CSVConverter<T, L extends ListParam<T>> extends AbstractHttpMessageConverter<L> {
    public CSVConverter() {
        super(new MediaType("text", "csv", StandardCharsets.UTF_8));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return ListParam.class.isAssignableFrom(clazz);
    }

    @Override
    protected L readInternal(Class<? extends L> clazz, HttpInputMessage inputMessage) throws IOException {
        HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
        Class<T> t = toBeanType(clazz.getGenericSuperclass());
        strategy.setType(t);

        CSVReader csv = new CSVReader(new InputStreamReader(inputMessage.getBody()));
        CsvToBean<T> csvToBean = new CsvToBean<>();
        csvToBean.setCsvReader(csv);
        csvToBean.setMappingStrategy(strategy);
        List<T> beanList = csvToBean.parse();

        try {
            L l = clazz.getDeclaredConstructor().newInstance();
            l.setList(beanList);
            return l;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void writeInternal(L l, HttpOutputMessage outputMessage) throws IOException {
        HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(toBeanType(l.getClass().getGenericSuperclass()));
        OutputStreamWriter outputStream = new OutputStreamWriter(outputMessage.getBody());
        StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(outputStream)
                .withQuotechar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withMappingStrategy(strategy)
                .build();

        try {
            beanToCsv.write(l.getList());
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<T> toBeanType(Type type) {
        return (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
    }
}
