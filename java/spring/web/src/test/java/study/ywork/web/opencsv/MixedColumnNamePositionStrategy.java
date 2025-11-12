package study.ywork.web.opencsv;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;


public class MixedColumnNamePositionStrategy<T> extends ColumnPositionMappingStrategy<T> {
    /**
     * 自定义列名和位置
     */
    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        super.generateHeader(bean);
        return super.getColumnMapping();
    }
}
