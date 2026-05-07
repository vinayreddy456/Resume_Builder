package com.resume.airesumoptimizer.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.Margin;
import org.springframework.stereotype.Service;

@Service
public class PlaywrightPdfService {

    /**
     * Generate PDF from HTML using Playwright (MIT License - Free Commercial Use)
     * Supports full CSS3: Flexbox, Grid, Gradients, Box-shadow, etc.
     */
    public byte[] generatePdf(String html) throws Exception {
        Playwright playwright = null;
        Browser browser = null;
        Page page = null;

        try {
            // 🚀 Initialize Playwright
            playwright = Playwright.create();
            
            // Launch Chromium (lightweight alternative to Chrome)
            browser = playwright.chromium().launch();
            
            // Create a new page
            page = browser.newPage();

            // Set HTML content
            page.setContent(html);

            // Wait for all resources to load
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Generate PDF with professional settings
            byte[] pdfBytes = page.pdf(new Page.PdfOptions()
                    .setFormat("A4")
                    .setMargin(new Margin()
                            .setTop("20px")
                            .setBottom("20px")
                            .setLeft("20px")
                            .setRight("20px"))
                    .setPrintBackground(true));  // Include background colors/images

            return pdfBytes;

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed with Playwright: " + e.getMessage(), e);
        } finally {
            // Cleanup
            if (page != null) page.close();
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();
        }
    }

    /**
     * Generate PDF from HTML with custom margins
     */
    public byte[] generatePdfWithMargins(String html, double top, double bottom, double left, double right) throws Exception {
        Playwright playwright = null;
        Browser browser = null;
        Page page = null;

        try {
            playwright = Playwright.create();
            browser = playwright.chromium().launch();
            page = browser.newPage();

            page.setContent(html);
            page.waitForLoadState(LoadState.NETWORKIDLE);

            byte[] pdfBytes = page.pdf(new Page.PdfOptions()
                    .setFormat("A4")
                    .setMargin(new Margin()
                            .setTop(toPixelMargin(top))
                            .setBottom(toPixelMargin(bottom))
                            .setLeft(toPixelMargin(left))
                            .setRight(toPixelMargin(right)))
                    .setPrintBackground(true));

            return pdfBytes;

        } finally {
            if (page != null) page.close();
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();
        }
    }

    private String toPixelMargin(double margin) {
        return margin + "px";
    }
}
