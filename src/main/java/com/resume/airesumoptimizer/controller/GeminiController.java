// package com.resume.airesumoptimizer.controller;

// import com.resume.airesumoptimizer.service.GeminiService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.Map;

// @RestController
// @RequestMapping("/api/gemini")
// @CrossOrigin(origins = "*")
// public class GeminiController {

//     @Autowired
//     private GeminiService geminiService;

//     @PostMapping("/enhance")
//     public ResponseEntity<Map<String, Object>> enhanceContent(
//             @RequestBody Map<String, String> request) {
        
//         try {
//             String originalContent = request.get("originalContent");
//             String jobDescription = request.get("jobDescription");
//             String analysisType = request.get("analysisType");
            
//             if (originalContent == null || jobDescription == null || analysisType == null) {
//                 return ResponseEntity.badRequest().body(Map.of(
//                     "success", false,
//                     "error", "Missing required parameters: originalContent, jobDescription, analysisType"
//                 ));
//             }
            
//             String enhancedContent = geminiService.enhanceResumeContent(
//                 originalContent, jobDescription, analysisType
//             );
            
//             return ResponseEntity.ok(Map.of(
//                 "success", true,
//                 "enhancedContent", enhancedContent
//             ));
            
//         } catch (Exception e) {
//             return ResponseEntity.badRequest().body(Map.of(
//                 "success", false,
//                 "error", e.getMessage()
//             ));
//         }
//     }

//     @PostMapping("/cover-letter")
//     public ResponseEntity<Map<String, Object>> generateCoverLetter(
//             @RequestBody Map<String, String> request) {
        
//         try {
//             String resumeContent = request.get("resumeContent");
//             String jobDescription = request.get("jobDescription");
            
//             if (resumeContent == null || jobDescription == null) {
//                 return ResponseEntity.badRequest().body(Map.of(
//                     "success", false,
//                     "error", "Missing required parameters: resumeContent, jobDescription"
//                 ));
//             }
            
//             String coverLetter = geminiService.generateCoverLetter(resumeContent, jobDescription);
            
//             return ResponseEntity.ok(Map.of(
//                 "success", true,
//                 "coverLetter", coverLetter
//             ));
            
//         } catch (Exception e) {
//             return ResponseEntity.badRequest().body(Map.of(
//                 "success", false,
//                 "error", e.getMessage()
//             ));
//         }
//     }

//     @PostMapping("/analyze-fit")
//     public ResponseEntity<Map<String, Object>> analyzeJobFit(
//             @RequestBody Map<String, String> request) {
        
//         try {
//             String resumeContent = request.get("resumeContent");
//             String jobDescription = request.get("jobDescription");
            
//             if (resumeContent == null || jobDescription == null) {
//                 return ResponseEntity.badRequest().body(Map.of(
//                     "success", false,
//                     "error", "Missing required parameters: resumeContent, jobDescription"
//                 ));
//             }
            
//             // String analysis = geminiService.analyzeJobFit(resumeContent, jobDescription);
            
//             return ResponseEntity.ok(Map.of(
//                 "success", true,
//                 "analysis", analysis
//             ));
            
//         } catch (Exception e) {
//             return ResponseEntity.badRequest().body(Map.of(
//                 "success", false,
//                 "error", e.getMessage()
//             ));
//         }
//     }
// }
