package com.resume.airesumoptimizer.agent;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.resume.airesumoptimizer.agent.ATSOptimizationAgent.ATSOptimizedResume;
import com.resume.airesumoptimizer.agent.SkillGapAnalyzerAgent.SkillGapAnalysis;

@Component
public class FormatterAgent {

    public FormattedResume formatResume(ATSOptimizedResume atsResume, SkillGapAnalysis skillGapAnalysis) {
        FormattedResume formatted = new FormattedResume();
        
        // Format each section according to professional standards
        formatted.setSummary(formatSection("PROFESSIONAL SUMMARY", atsResume.getSummary()));
        formatted.setSkills(formatSkillsSection(atsResume.getSkills()));
        formatted.setExperience(formatExperienceSection(atsResume.getExperience()));
        formatted.setProjects(formatProjectsSection(atsResume.getProjects()));
        formatted.setEducation(formatEducationSection(atsResume.getEducation()));
        formatted.setCertifications(formatCertificationsSection(atsResume.getCertifications()));
        
        // Add additional improvements section
        formatted.setAdditionalImprovements(formatAdditionalImprovements(skillGapAnalysis));
        
        // Generate complete resume text
        formatted.setFullResume(generateFullResume(formatted));
        
        // Calculate resume statistics
        formatted.setResumeStats(calculateResumeStats(formatted, atsResume));
        
        return formatted;
    }

    private String formatSection(String title, String content) {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }
        
        StringBuilder formatted = new StringBuilder();
        formatted.append(title.toUpperCase()).append("\n");
        formatted.append("=".repeat(title.length())).append("\n\n");
        formatted.append(content.trim()).append("\n\n");
        
        return formatted.toString();
    }

    private String formatSkillsSection(Map<String, List<String>> skills) {
        if (skills == null || skills.isEmpty()) {
            return "";
        }
        
        StringBuilder formatted = new StringBuilder();
        formatted.append("TECHNICAL SKILLS\n");
        formatted.append("===============").append("\n\n");
        
        for (Map.Entry<String, List<String>> entry : skills.entrySet()) {
            formatted.append("**").append(entry.getKey()).append(":** ");
            formatted.append(String.join(", ", entry.getValue())).append("\n\n");
        }
        
        return formatted.toString();
    }

    private String formatExperienceSection(List<ATSOptimizedResume.ATSOptimizedExperience> experience) {
        if (experience == null || experience.isEmpty()) {
            return "";
        }
        
        StringBuilder formatted = new StringBuilder();
        formatted.append("PROFESSIONAL EXPERIENCE\n");
        formatted.append("=====================").append("\n\n");
        
        for (ATSOptimizedResume.ATSOptimizedExperience exp : experience) {
            // Company and position header
            formatted.append("**").append(exp.getPosition()).append("**");
            if (exp.getCompany() != null) {
                formatted.append(" | ").append(exp.getCompany());
            }
            if (exp.getDuration() != null) {
                formatted.append(" | ").append(exp.getDuration());
            }
            if (exp.getLocation() != null) {
                formatted.append(" | ").append(exp.getLocation());
            }
            formatted.append("\n\n");
            
            // Responsibilities as bullet points
            if (exp.getResponsibilities() != null && !exp.getResponsibilities().isEmpty()) {
                for (String responsibility : exp.getResponsibilities()) {
                    formatted.append("• ").append(responsibility).append("\n");
                }
                formatted.append("\n");
            }
        }
        
        return formatted.toString();
    }

    private String formatProjectsSection(List<ATSOptimizedResume.ATSOptimizedProject> projects) {
        if (projects == null || projects.isEmpty()) {
            return "";
        }
        
        StringBuilder formatted = new StringBuilder();
        formatted.append("PROJECTS\n");
        formatted.append("========").append("\n\n");
        
        for (ATSOptimizedResume.ATSOptimizedProject project : projects) {
            // Project name and description
            formatted.append("**").append(project.getName()).append("**\n");
            if (project.getDescription() != null && !project.getDescription().trim().isEmpty()) {
                formatted.append(project.getDescription()).append("\n");
            }
            
            // Technologies
            if (project.getTechnologies() != null && !project.getTechnologies().isEmpty()) {
                formatted.append("*Technologies:* ").append(String.join(", ", project.getTechnologies())).append("\n");
            }
            
            // Achievements as bullet points
            if (project.getAchievements() != null && !project.getAchievements().isEmpty()) {
                formatted.append("\n");
                for (String achievement : project.getAchievements()) {
                    formatted.append("• ").append(achievement).append("\n");
                }
            }
            
            formatted.append("\n");
        }
        
        return formatted.toString();
    }

    private String formatEducationSection(List<com.resume.airesumoptimizer.model.ResumeData.Education> education) {
        if (education == null || education.isEmpty()) {
            return "";
        }
        
        StringBuilder formatted = new StringBuilder();
        formatted.append("EDUCATION\n");
        formatted.append("=========").append("\n\n");
        
        for (com.resume.airesumoptimizer.model.ResumeData.Education edu : education) {
            formatted.append("**").append(edu.getDegree()).append("**");
            if (edu.getField() != null) {
                formatted.append(" in ").append(edu.getField());
            }
            if (edu.getInstitution() != null) {
                formatted.append(" | ").append(edu.getInstitution());
            }
            if (edu.getGraduationYear() != null) {
                formatted.append(" | ").append(edu.getGraduationYear());
            }
            if (edu.getGpa() != null) {
                formatted.append(" | GPA: ").append(edu.getGpa());
            }
            formatted.append("\n\n");
        }
        
        return formatted.toString();
    }

    private String formatCertificationsSection(List<String> certifications) {
        if (certifications == null || certifications.isEmpty()) {
            return "";
        }
        
        StringBuilder formatted = new StringBuilder();
        formatted.append("CERTIFICATIONS\n");
        formatted.append("==============").append("\n\n");
        
        for (String certification : certifications) {
            formatted.append("• ").append(certification).append("\n");
        }
        formatted.append("\n");
        
        return formatted.toString();
    }

    private String formatAdditionalImprovements(SkillGapAnalysis skillGapAnalysis) {
        StringBuilder improvements = new StringBuilder();
        improvements.append("ADDITIONAL IMPROVEMENTS\n");
        improvements.append("======================").append("\n\n");
        
        // Missing skills section
        if (skillGapAnalysis.getMissingRequiredSkills() != null && !skillGapAnalysis.getMissingRequiredSkills().isEmpty()) {
            improvements.append("**Missing Required Skills to Learn:**\n");
            for (String skill : skillGapAnalysis.getMissingRequiredSkills()) {
                improvements.append("• ").append(skill).append("\n");
            }
            improvements.append("\n");
        }
        
        if (skillGapAnalysis.getMissingPreferredSkills() != null && !skillGapAnalysis.getMissingPreferredSkills().isEmpty()) {
            improvements.append("**Missing Preferred Skills to Learn:**\n");
            for (String skill : skillGapAnalysis.getMissingPreferredSkills()) {
                improvements.append("• ").append(skill).append("\n");
            }
            improvements.append("\n");
        }
        
        // Experience gap
        if (!skillGapAnalysis.getExperienceMatch().isMeetsRequirement()) {
            improvements.append("**Experience Gap:**\n");
            improvements.append("• ").append(skillGapAnalysis.getExperienceMatch().getMatchDescription()).append("\n\n");
        }
        
        // General suggestions
        if (skillGapAnalysis.getImprovementSuggestions() != null && !skillGapAnalysis.getImprovementSuggestions().isEmpty()) {
            improvements.append("**General Improvement Suggestions:**\n");
            for (String suggestion : skillGapAnalysis.getImprovementSuggestions()) {
                improvements.append("• ").append(suggestion).append("\n");
            }
            improvements.append("\n");
        }
        
        // Overall fit assessment
        improvements.append("**Overall Fit Assessment:**\n");
        improvements.append("• ").append(skillGapAnalysis.getOverallFit()).append("\n");
        improvements.append("• Overall Match: ").append(skillGapAnalysis.getOverallMatchPercentage()).append("%\n");
        improvements.append("• Required Skills Match: ").append(skillGapAnalysis.getRequiredMatchPercentage()).append("%\n");
        improvements.append("• Preferred Skills Match: ").append(skillGapAnalysis.getPreferredMatchPercentage()).append("%\n\n");
        
        return improvements.toString();
    }

    private String generateFullResume(FormattedResume formatted) {
        StringBuilder fullResume = new StringBuilder();
        
        // Add all sections in order
        if (formatted.getSummary() != null && !formatted.getSummary().isEmpty()) {
            fullResume.append(formatted.getSummary());
        }
        
        if (formatted.getSkills() != null && !formatted.getSkills().isEmpty()) {
            fullResume.append(formatted.getSkills());
        }
        
        if (formatted.getExperience() != null && !formatted.getExperience().isEmpty()) {
            fullResume.append(formatted.getExperience());
        }
        
        if (formatted.getProjects() != null && !formatted.getProjects().isEmpty()) {
            fullResume.append(formatted.getProjects());
        }
        
        if (formatted.getEducation() != null && !formatted.getEducation().isEmpty()) {
            fullResume.append(formatted.getEducation());
        }
        
        if (formatted.getCertifications() != null && !formatted.getCertifications().isEmpty()) {
            fullResume.append(formatted.getCertifications());
        }
        
        return fullResume.toString().trim();
    }

    private ResumeStats calculateResumeStats(FormattedResume formatted, ATSOptimizationAgent.ATSOptimizedResume atsResume) {
        ResumeStats stats = new ResumeStats();
        
        String fullText = formatted.getFullResume();
        
        // Word count
        stats.setWordCount(fullText.split("\\s+").length);
        
        // Character count
        stats.setCharacterCount(fullText.length());
        
        // Section counts
        stats.setSectionCount(countSections(fullText));
        
        // Bullet point count
        stats.setBulletPointCount(countBulletPoints(fullText));
        
        // Skills count
        if (atsResume.getSkills() != null) {
            int skillsCount = atsResume.getSkills().values().stream()
                .mapToInt(List::size)
                .sum();
            stats.setSkillsCount(skillsCount);
        }
        
        // Experience count
        if (atsResume.getExperience() != null) {
            stats.setExperienceCount(atsResume.getExperience().size());
        }
        
        // Project count
        if (atsResume.getProjects() != null) {
            stats.setProjectCount(atsResume.getProjects().size());
        }
        
        // Page estimate (rough calculation: ~500 words per page)
        stats.setEstimatedPages((int) Math.ceil(stats.getWordCount() / 500.0));
        
        return stats;
    }

    private int countSections(String text) {
        return (int) text.lines()
            .filter(line -> line.matches("^[A-Z\\s]+$"))
            .count();
    }

    private int countBulletPoints(String text) {
        return (int) text.lines()
            .filter(line -> line.trim().startsWith("•"))
            .count();
    }

    public static class FormattedResume {
        private String summary;
        private String skills;
        private String experience;
        private String projects;
        private String education;
        private String certifications;
        private String additionalImprovements;
        private String fullResume;
        private ResumeStats resumeStats;

        // Getters and Setters
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }

        public String getSkills() { return skills; }
        public void setSkills(String skills) { this.skills = skills; }

        public String getExperience() { return experience; }
        public void setExperience(String experience) { this.experience = experience; }

        public String getProjects() { return projects; }
        public void setProjects(String projects) { this.projects = projects; }

        public String getEducation() { return education; }
        public void setEducation(String education) { this.education = education; }

        public String getCertifications() { return certifications; }
        public void setCertifications(String certifications) { this.certifications = certifications; }

        public String getAdditionalImprovements() { return additionalImprovements; }
        public void setAdditionalImprovements(String additionalImprovements) { this.additionalImprovements = additionalImprovements; }

        public String getFullResume() { return fullResume; }
        public void setFullResume(String fullResume) { this.fullResume = fullResume; }

        public ResumeStats getResumeStats() { return resumeStats; }
        public void setResumeStats(ResumeStats resumeStats) { this.resumeStats = resumeStats; }
    }

    public static class ResumeStats {
        private int wordCount;
        private int characterCount;
        private int sectionCount;
        private int bulletPointCount;
        private int skillsCount;
        private int experienceCount;
        private int projectCount;
        private int estimatedPages;

        // Getters and Setters
        public int getWordCount() { return wordCount; }
        public void setWordCount(int wordCount) { this.wordCount = wordCount; }

        public int getCharacterCount() { return characterCount; }
        public void setCharacterCount(int characterCount) { this.characterCount = characterCount; }

        public int getSectionCount() { return sectionCount; }
        public void setSectionCount(int sectionCount) { this.sectionCount = sectionCount; }

        public int getBulletPointCount() { return bulletPointCount; }
        public void setBulletPointCount(int bulletPointCount) { this.bulletPointCount = bulletPointCount; }

        public int getSkillsCount() { return skillsCount; }
        public void setSkillsCount(int skillsCount) { this.skillsCount = skillsCount; }

        public int getExperienceCount() { return experienceCount; }
        public void setExperienceCount(int experienceCount) { this.experienceCount = experienceCount; }

        public int getProjectCount() { return projectCount; }
        public void setProjectCount(int projectCount) { this.projectCount = projectCount; }

        public int getEstimatedPages() { return estimatedPages; }
        public void setEstimatedPages(int estimatedPages) { this.estimatedPages = estimatedPages; }
    }
}
