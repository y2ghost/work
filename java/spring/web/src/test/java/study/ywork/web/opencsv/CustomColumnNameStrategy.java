package study.ywork.web.opencsv;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.util.Arrays;

/**
 * 自定义列名映射策略类
 */
public class CustomColumnNameStrategy<T> extends HeaderColumnNameMappingStrategy<T> {
    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        String[] header = super.generateHeader(bean);
        return Arrays.stream(header)
                .map(String::toLowerCase)
                .toArray(String[]::new);
    }
}
