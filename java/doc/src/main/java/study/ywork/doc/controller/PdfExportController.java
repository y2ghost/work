package study.ywork.doc.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import study.ywork.doc.service.PdfExportService;

import java.io.IOException;

@Controller
@RequestMapping("/pdf-export")
public class PdfExportController {
    private final PdfExportService pdfExportService;

    public PdfExportController(PdfExportService pdfExportService) {
        this.pdfExportService = pdfExportService;
    }

    @GetMapping("/index.html")
    public String index() {
        return "pdf-index";
    }

    @PostMapping("/export")
    public void generatePdf(@RequestParam String name,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        String userAgent = request.getHeader("User-Agent");
        pdfExportService.exportPdf(response, name, userAgent);
    }
}
