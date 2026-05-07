package com.resume.airesumoptimizer.controller;

import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.resume.airesumoptimizer.service.OpenAIService;
import com.resume.airesumoptimizer.service.ResumeHtmlService;
import com.resume.airesumoptimizer.service.PdfService;

@RestController
@RequestMapping("/resume")
@CrossOrigin(origins = "*")
public class ResumeOptimizationController {

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private ResumeHtmlService htmlService;   // ✅ NEW

    @Autowired
    private PdfService pdfService;           // ✅ NEW

    // 🔥 STEP 1: ANALYZE + OPTIMIZE
    @PostMapping("/optimize")
    public ResponseEntity<Map<String, Object>> optimizeResume(
            @RequestParam("resumeFile") MultipartFile resumeFile,
            @RequestParam("jobDescription") String jobDescription) {

        try {
            String resumeText = extractText(resumeFile);

            Map<String, Object> result =
                    openAIService.optimizeResume(resumeText, jobDescription);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

     private String extractText(MultipartFile file) throws Exception {
      PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return text;
    }

    // 🔥 STEP 3: JSON → HTML → PDF → DOWNLOAD
    @PostMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestBody Map<String, Object> data) {

        try {
            if (data == null || data.isEmpty()) {
                throw new RuntimeException("Resume data is empty");
            }

            // ✅ Convert JSON → HTML
            String html = htmlService.generateHtml(data);

            // ✅ Convert HTML → PDF
            byte[] pdf = pdfService.generatePdf(html);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=resume.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "healthy"));
    }
}