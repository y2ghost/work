package study.ywork.doc.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import study.ywork.doc.domain.CurrencyRate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Map;

/*
 * Excel文件视图例子
 */
public class ExcelView extends AbstractXlsxView {
    @SuppressWarnings("unchecked")
    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      Workbook workbook,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        List<CurrencyRate> currencyRates = (List<CurrencyRate>) model.get("todayCurrencyRates");
        Sheet sheet = workbook.createSheet("Today Currency Rates");
        sheet.setFitToPage(true);
        int rowCount = 0;
        Row header = sheet.createRow(rowCount++);
        header.createCell(0).setCellValue("Currency Pair");
        header.createCell(1).setCellValue("Bid Price");
        header.createCell(2).setCellValue("Ask Price");
        header.createCell(3).setCellValue("Date");

        for (CurrencyRate currencyRate : currencyRates) {
            Row currencyRow = sheet.createRow(rowCount++);
            currencyRow.createCell(0).setCellValue(currencyRate.getCurrencyPair());
            currencyRow.createCell(1).setCellValue(currencyRate.getBidPrice().doubleValue());
            currencyRow.createCell(2).setCellValue(currencyRate.getAskPrice().doubleValue());
            currencyRow.createCell(3).setCellValue(formatDate(currencyRate.getDateTime()));
        }

        response.setHeader("Content-Disposition", "attachment; filename=currency-rates.xlsx");
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
    }
}
