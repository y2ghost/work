package study.ywork.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import study.ywork.web.domain.Report;

@Controller
@RequestMapping("/pdf")
public class PdfViewController {
    @GetMapping
    public String get(Model model) {
        model.addAttribute("report", getReport());
        return "appPdfView";
    }

    private Report getReport() {
        // 测试数据
        Report report = new Report();
        report.setId(100);
        report.setReportName("My Report");
        report.setContent("this is an example for pdf view");
        return report;
    }
}
