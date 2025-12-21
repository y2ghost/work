package study.ywork.doc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import study.ywork.doc.domain.Report;
import study.ywork.doc.view.PdfView;

@Controller
@RequestMapping("/pdf")
public class PdfViewController {
    private final PdfView pdfView;

    public PdfViewController(PdfView pdfView) {
        this.pdfView = pdfView;
    }

    @GetMapping
    public View get(Model model) {
        model.addAttribute("report", getReport());
        return pdfView;
    }

    private Report getReport() {
        // 测试数据
        Report report = new Report();
        report.setId(100);
        report.setReportName("My Report");
        report.setContent("This is a example pdf document");
        return report;
    }
}
