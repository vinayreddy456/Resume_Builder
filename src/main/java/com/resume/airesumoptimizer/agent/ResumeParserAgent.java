package com.resume.airesumoptimizer.agent;

import com.resume.airesumoptimizer.model.ResumeData;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ResumeParserAgent {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "(\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}"
    );

    public ResumeData parseResume(MultipartFile file) throws IOException {
        String content = extractTextFromFile(file);
        return parseResumeContent(content);
    }

    private String extractTextFromFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        try (InputStream inputStream = file.getInputStream()) {
            switch (extension) {
                case "pdf":
                    return extractFromPDF(inputStream);
                case "doc":
                    return extractFromDoc(inputStream);
                case "docx":
                    return extractFromDocx(inputStream);
                case "txt":
                    return new String(inputStream.readAllBytes());
                default:
                    throw new IllegalArgumentException("Unsupported file format: " + extension);
            }
        }
    }

    private String extractFromPDF(InputStream inputStream) throws IOException {
        PDDocument document = PDDocument.load(inputStream);
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } finally {
            document.close();
        }
    }

    private String extractFromDoc(InputStream inputStream) throws IOException {
        try (HWPFDocument document = new HWPFDocument(inputStream);
             WordExtractor extractor = new WordExtractor(document)) {
            return extractor.getText();
        }
    }

    private String extractFromDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            StringBuilder text = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                text.append(paragraph.getText()).append("\n");
            }
            return text.toString();
        }
    }

    private ResumeData parseResumeContent(String content) {
        ResumeData resumeData = new ResumeData();
        
        // Extract contact information
        resumeData.setEmail(extractEmail(content));
        resumeData.setPhone(extractPhone(content));
        resumeData.setName(extractName(content));
        
        // Extract sections
        resumeData.setSummary(extractSection(content, Arrays.asList("summary", "objective", "profile")));
        resumeData.setExperience(extractExperience(content));
        resumeData.setEducation(extractEducation(content));
        resumeData.setProjects(extractProjects(content));
        resumeData.setSkills(extractSkills(content));
        resumeData.setCertifications(extractCertifications(content));
        
        return resumeData;
    }

    private String extractEmail(String content) {
        Matcher matcher = EMAIL_PATTERN.matcher(content);
        return matcher.find() ? matcher.group() : null;
    }

    private String extractPhone(String content) {
        Matcher matcher = PHONE_PATTERN.matcher(content);
        return matcher.find() ? matcher.group() : null;
    }

    private String extractName(String content) {
        // Simple name extraction - looks for capitalized words at the beginning
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && !line.toLowerCase().contains("resume") && 
                !line.toLowerCase().contains("curriculum") && line.length() < 50) {
                // Check if it looks like a name (2-3 capitalized words)
                String[] words = line.split("\\s+");
                if (words.length >= 2 && words.length <= 4) {
                    boolean allCapitalized = true;
                    for (String word : words) {
                        if (word.length() > 0 && !Character.isUpperCase(word.charAt(0))) {
                            allCapitalized = false;
                            break;
                        }
                    }
                    if (allCapitalized) {
                        return line;
                    }
                }
            }
        }
        return null;
    }

    private String extractSection(String content, List<String> keywords) {
        String[] lines = content.split("\n");
        StringBuilder section = new StringBuilder();
        boolean inSection = false;

        for (String line : lines) {
            String lowerLine = line.toLowerCase().trim();
            
            // Check if this line contains a section keyword
            for (String keyword : keywords) {
                if (lowerLine.contains(keyword)) {
                    inSection = true;
                    continue;
                }
            }
            
            // Check if we've reached another section
            if (inSection && (lowerLine.contains("experience") || lowerLine.contains("education") || 
                lowerLine.contains("skills") || lowerLine.contains("projects"))) {
                break;
            }
            
            if (inSection && !line.trim().isEmpty()) {
                section.append(line).append("\n");
            }
        }
        
        return section.length() > 0 ? section.toString().trim() : null;
    }

    private List<ResumeData.Experience> extractExperience(String content) {
        List<ResumeData.Experience> experiences = new ArrayList<>();
        String[] lines = content.split("\n");
        
        ResumeData.Experience currentExperience = null;
        boolean inExperienceSection = false;
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            String lowerLine = line.toLowerCase();
            
            if (lowerLine.contains("experience") || lowerLine.contains("work")) {
                inExperienceSection = true;
                continue;
            }
            
            if (inExperienceSection && (lowerLine.contains("education") || 
                lowerLine.contains("skills") || lowerLine.contains("projects"))) {
                break;
            }
            
            if (inExperienceSection && !line.isEmpty()) {
                // Look for patterns like "Company Name - Position" or "Position at Company"
                if (line.contains("-") || line.contains("at") || line.contains("@")) {
                    if (currentExperience != null) {
                        experiences.add(currentExperience);
                    }
                    currentExperience = new ResumeData.Experience();
                    
                    // Extract company and position
                    if (line.contains("-")) {
                        String[] parts = line.split("-", 2);
                        currentExperience.setCompany(parts[0].trim());
                        currentExperience.setPosition(parts[1].trim());
                    } else if (line.contains("at")) {
                        String[] parts = line.split("at", 2);
                        currentExperience.setPosition(parts[0].trim());
                        currentExperience.setCompany(parts[1].trim());
                    }
                    
                    // Look for duration in next few lines
                    for (int j = i + 1; j < Math.min(i + 3, lines.length); j++) {
                        String nextLine = lines[j].trim();
                        if (nextLine.matches(".*\\d{4}.*") || nextLine.toLowerCase().contains("present") ||
                            nextLine.toLowerCase().contains("year") || nextLine.contains("-")) {
                            currentExperience.setDuration(nextLine);
                            break;
                        }
                    }
                } else if (currentExperience != null && line.startsWith("•") || line.startsWith("-") || line.startsWith("*")) {
                    // This is a responsibility bullet point
                    if (currentExperience.getResponsibilities() == null) {
                        currentExperience.setResponsibilities(new ArrayList<>());
                    }
                    currentExperience.getResponsibilities().add(line.substring(1).trim());
                }
            }
        }
        
        if (currentExperience != null) {
            experiences.add(currentExperience);
        }
        
        return experiences;
    }

    private List<ResumeData.Education> extractEducation(String content) {
        List<ResumeData.Education> educationList = new ArrayList<>();
        String[] lines = content.split("\n");
        
        boolean inEducationSection = false;
        
        for (String line : lines) {
            String lowerLine = line.toLowerCase().trim();
            
            if (lowerLine.contains("education") || lowerLine.contains("academic")) {
                inEducationSection = true;
                continue;
            }
            
            if (inEducationSection && (lowerLine.contains("experience") || 
                lowerLine.contains("skills") || lowerLine.contains("projects"))) {
                break;
            }
            
            if (inEducationSection && !line.isEmpty()) {
                // Look for education patterns
                if (lowerLine.contains("university") || lowerLine.contains("college") || 
                    lowerLine.contains("institute") || lowerLine.contains("bachelor") || 
                    lowerLine.contains("master") || lowerLine.contains("phd")) {
                    
                    ResumeData.Education education = new ResumeData.Education();
                    education.setInstitution(line);
                    
                    // Try to extract degree and field
                    if (lowerLine.contains("bachelor")) {
                        education.setDegree("Bachelor's");
                    } else if (lowerLine.contains("master")) {
                        education.setDegree("Master's");
                    } else if (lowerLine.contains("phd")) {
                        education.setDegree("PhD");
                    }
                    
                    educationList.add(education);
                }
            }
        }
        
        return educationList;
    }

    private List<ResumeData.Project> extractProjects(String content) {
        List<ResumeData.Project> projects = new ArrayList<>();
        String[] lines = content.split("\n");
        
        boolean inProjectsSection = false;
        ResumeData.Project currentProject = null;
        
        for (String line : lines) {
            String lowerLine = line.toLowerCase().trim();
            
            if (lowerLine.contains("project")) {
                inProjectsSection = true;
                continue;
            }
            
            if (inProjectsSection && (lowerLine.contains("experience") || 
                lowerLine.contains("education") || lowerLine.contains("skills"))) {
                break;
            }
            
            if (inProjectsSection && !line.isEmpty()) {
                if (!line.startsWith("•") && !line.startsWith("-") && !line.startsWith("*")) {
                    // This might be a project title
                    if (currentProject != null) {
                        projects.add(currentProject);
                    }
                    currentProject = new ResumeData.Project();
                    currentProject.setName(line);
                } else if (currentProject != null) {
                    // This is a project detail
                    String detail = line.substring(1).trim();
                    if (currentProject.getDescription() == null) {
                        currentProject.setDescription(detail);
                    } else {
                        // Add to achievements or technologies
                        if (detail.toLowerCase().contains("technology") || 
                            detail.toLowerCase().contains("tech stack") ||
                            detail.toLowerCase().contains("used")) {
                            if (currentProject.getTechnologies() == null) {
                                currentProject.setTechnologies(new ArrayList<>());
                            }
                            currentProject.getTechnologies().add(detail);
                        } else {
                            if (currentProject.getAchievements() == null) {
                                currentProject.setAchievements(new ArrayList<>());
                            }
                            currentProject.getAchievements().add(detail);
                        }
                    }
                }
            }
        }
        
        if (currentProject != null) {
            projects.add(currentProject);
        }
        
        return projects;
    }

    private List<String> extractSkills(String content) {
        List<String> skills = new ArrayList<>();
        String[] lines = content.split("\n");
        
        boolean inSkillsSection = false;
        
        for (String line : lines) {
            String lowerLine = line.toLowerCase().trim();
            
            if (lowerLine.contains("skill") || lowerLine.contains("technical") || 
                lowerLine.contains("technologies")) {
                inSkillsSection = true;
                continue;
            }
            
            if (inSkillsSection && (lowerLine.contains("experience") || 
                lowerLine.contains("education") || lowerLine.contains("projects"))) {
                break;
            }
            
            if (inSkillsSection && !line.isEmpty()) {
                // Extract skills from comma-separated lists or bullet points
                if (line.contains(",")) {
                    String[] skillArray = line.split(",");
                    for (String skill : skillArray) {
                        String trimmedSkill = skill.trim();
                        if (!trimmedSkill.isEmpty() && !trimmedSkill.toLowerCase().contains("skill")) {
                            skills.add(trimmedSkill);
                        }
                    }
                } else if (line.startsWith("•") || line.startsWith("-") || line.startsWith("*")) {
                    String skill = line.substring(1).trim();
                    if (!skill.isEmpty()) {
                        skills.add(skill);
                    }
                } else if (!line.toLowerCase().contains("skill") && line.length() < 50) {
                    skills.add(line);
                }
            }
        }
        
        return skills;
    }

    private List<String> extractCertifications(String content) {
        List<String> certifications = new ArrayList<>();
        String[] lines = content.split("\n");
        
        boolean inCertificationsSection = false;
        
        for (String line : lines) {
            String lowerLine = line.toLowerCase().trim();
            
            if (lowerLine.contains("certification") || lowerLine.contains("certificate") || 
                lowerLine.contains("license")) {
                inCertificationsSection = true;
                continue;
            }
            
            if (inCertificationsSection && (lowerLine.contains("experience") || 
                lowerLine.contains("education") || lowerLine.contains("skills"))) {
                break;
            }
            
            if (inCertificationsSection && !line.isEmpty()) {
                if (line.startsWith("•") || line.startsWith("-") || line.startsWith("*")) {
                    certifications.add(line.substring(1).trim());
                } else if (!line.toLowerCase().contains("certification")) {
                    certifications.add(line);
                }
            }
        }
        
        return certifications;
    }
}
