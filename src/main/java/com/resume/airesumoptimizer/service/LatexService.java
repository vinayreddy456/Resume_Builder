package com.resume.airesumoptimizer.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class LatexService {

    public byte[] generatePdf(String latexContent) {
        try {
            // 🔥 1. Create temp directory
            Path tempDir = Files.createTempDirectory("latex");

            // 🔥 2. Create .tex file
            File texFile = new File(tempDir.toFile(), "resume.tex");
            try (FileWriter writer = new FileWriter(texFile)) {
                writer.write(latexContent);
            }

            // 🔥 3. Use FULL PATH to pdflatex (YOUR VERIFIED PATH)
            ProcessBuilder pb = new ProcessBuilder(
                    "C:\\Users\\veera\\AppData\\Local\\Programs\\MiKTeX\\miktex\\bin\\x64\\pdflatex.exe",
                    "-interaction=nonstopmode",
                    "resume.tex" // ✅ IMPORTANT (not absolute path)
            );

            pb.directory(tempDir.toFile());
            pb.redirectErrorStream(true);

            // 🔥 4. Start process
            Process process = pb.start();

            // 🔥 5. Print LaTeX logs (VERY IMPORTANT)
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 🔥 6. Wait for completion
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("LaTeX compilation failed");
            }

            // 🔥 7. Read generated PDF
            File pdfFile = new File(tempDir.toFile(), "resume.pdf");

            if (!pdfFile.exists()) {
                throw new RuntimeException("PDF not generated");
            }

            return Files.readAllBytes(pdfFile.toPath());

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }
}