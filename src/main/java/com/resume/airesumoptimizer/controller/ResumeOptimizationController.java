// package com.resume.airesumoptimizer.controller;

// import com.resume.airesumoptimizer.service.GeminiService;
// import com.resume.airesumoptimizer.service.OpenAIService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

// import org.apache.pdfbox.pdmodel.PDDocument;
// import org.apache.pdfbox.text.PDFTextStripper;

// import java.util.Map;

// @RestController
// @RequestMapping("/resume")
// @CrossOrigin(origins = "*")
// public class ResumeOptimizationController {

//     @Autowired
//     private GeminiService geminiService;
//     @Autowired
//     private OpenAIService openAIService;



//     /**
//      * 🔥 MAIN API → Optimize Resume using AI
//      */
//     @PostMapping("/optimize")
//     public ResponseEntity<Map<String, Object>> optimizeResume(
//             @RequestParam("resumeFile") MultipartFile resumeFile,
//             @RequestParam("jobDescription") String jobDescription) {

//         try {
//             // Step 1: Extract resume text
//             String resumeText = extractText(resumeFile);

//             // Step 2: Call Gemini AI (returns Map)
//             Map<String, Object> aiResult =
//                     openAIService.optimizeResume(resumeText, jobDescription);

//             // Step 3: Return structured response
//             return ResponseEntity.ok(Map.of(
//                     "success", true,
//                     "data", aiResult
//             ));

//         } catch (Exception e) {
//             return ResponseEntity.badRequest().body(Map.of(
//                     "success", false,
//                     "error", e.getMessage()
//             ));
//         }
//     }

//     /**
//      * 🔍 Extract text from PDF resume
//      */
//     private String extractText(MultipartFile file) throws Exception {
//         PDDocument document = PDDocument.load(file.getInputStream());
//         PDFTextStripper stripper = new PDFTextStripper();
//         String text = stripper.getText(document);
//         document.close();
//         return text;
//     }

//     /**
//      * ✅ Health Check API
//      */
//     @GetMapping("/health")
//     public ResponseEntity<Map<String, String>> health() {
//         return ResponseEntity.ok(Map.of(
//                 "status", "healthy"
//         ));
//     }
// }


package com.resume.airesumoptimizer.controller;

import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.resume.airesumoptimizer.service.LatexService;
import com.resume.airesumoptimizer.service.OpenAIService;

@RestController
@RequestMapping("/resume")
@CrossOrigin(origins = "*")
public class ResumeOptimizationController {

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private LatexService latexService;

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

   @PostMapping("/download-pdf")
public ResponseEntity<byte[]> downloadPdf(@RequestBody Map<String, String> request) {

    String latex = request.get("latex");

    if (latex == null || latex.trim().isEmpty()) {
        throw new RuntimeException("Latex content is empty");
    }

    byte[] pdf = latexService.generatePdf(latex);

    return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=resume.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
}

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "healthy"));
    }
}