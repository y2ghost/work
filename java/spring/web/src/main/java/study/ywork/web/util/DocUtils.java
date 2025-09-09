package study.ywork.web.utils;

import java.util.UUID;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class DocUtils {
    // 示例只读权限的文档，自动换行和左对齐
    public static void xlsxOptions() {
        ExcelWriter writer = ExcelUtil.getWriter(true);
        XSSFSheet xslxSheet = (XSSFSheet) writer.getSheet();
        xslxSheet.protectSheet(UUID.randomUUID().toString());
        xslxSheet.lockSelectLockedCells(true);
        CellStyle cellStyle = writer.getStyleSet().getCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        // 标题别名
        // writer.addHeaderAlias("code", "代码");
        // writer.addHeaderAlias("name", "名称");
        // 写入数据
        // writer.setOnlyAlias(true);
        // writer.write(dataList, true);
        // 自动调整列宽
        writer.autoSizeColumnAll();
        // 设置默认列宽
        writer.setColumnWidth(-1, 30);
    }
}

