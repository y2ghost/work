package study.ywork.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import study.ywork.web.domain.Report;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ReportController {
    private List<Report> reports = new ArrayList<>();

    @PostMapping(value = "/reports", consumes = "text/report", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String handleRequest(@RequestBody Report report) {
        report.setId(reports.size() + 1);
        reports.add(report);
        return "报告已存: " + report;
    }

    @GetMapping(value = "/reports/{id}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Report reportById(@PathVariable("id") int reportId) {
        if (reportId > reports.size()) {
            throw new RuntimeException("报告ID未找到:" + reportId);
        }

        return reports.get(reportId - 1);
    }
}
