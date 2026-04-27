package com.resume.airesumoptimizer.agent;

import com.resume.airesumoptimizer.model.ResumeData;
import com.resume.airesumoptimizer.model.JobDescription;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SkillGapAnalyzerAgent {

    public SkillGapAnalysis analyzeSkillGap(ResumeData resume, JobDescription jobDescription) {
        SkillGapAnalysis analysis = new SkillGapAnalysis();
        
        // Get all skills from resume
        Set<String> resumeSkills = extractAllResumeSkills(resume);
        
        // Get required and preferred skills from job
        Set<String> requiredSkills = new HashSet<>(jobDescription.getRequiredSkills() != null ? 
            jobDescription.getRequiredSkills() : Collections.emptyList());
        Set<String> preferredSkills = new HashSet<>(jobDescription.getPreferredSkills() != null ? 
            jobDescription.getPreferredSkills() : Collections.emptyList());
        
        // Find matching skills
        Set<String> matchingRequiredSkills = findMatchingSkills(resumeSkills, requiredSkills);
        Set<String> matchingPreferredSkills = findMatchingSkills(resumeSkills, preferredSkills);
        
        // Find missing skills
        Set<String> missingRequiredSkills = new HashSet<>(requiredSkills);
        missingRequiredSkills.removeAll(matchingRequiredSkills);
        
        Set<String> missingPreferredSkills = new HashSet<>(preferredSkills);
        missingPreferredSkills.removeAll(matchingPreferredSkills);
        
        // Calculate match percentage
        double requiredMatchPercentage = requiredSkills.isEmpty() ? 0.0 : 
            (double) matchingRequiredSkills.size() / requiredSkills.size() * 100;
        double preferredMatchPercentage = preferredSkills.isEmpty() ? 0.0 : 
            (double) matchingPreferredSkills.size() / preferredSkills.size() * 100;
        double overallMatchPercentage = (requiredSkills.size() + preferredSkills.size()) == 0 ? 0.0 :
            (double) (matchingRequiredSkills.size() + matchingPreferredSkills.size()) / 
            (requiredSkills.size() + preferredSkills.size()) * 100;
        
        // Analyze experience match
        ExperienceMatch experienceMatch = analyzeExperienceMatch(resume, jobDescription);
        
        // Analyze education match
        EducationMatch educationMatch = analyzeEducationMatch(resume, jobDescription);
        
        // Generate improvement suggestions
        List<String> suggestions = generateImprovementSuggestions(
            missingRequiredSkills, missingPreferredSkills, experienceMatch, educationMatch
        );
        
        // Populate analysis object
        analysis.setResumeSkills(new ArrayList<>(resumeSkills));
        analysis.setMatchingRequiredSkills(new ArrayList<>(matchingRequiredSkills));
        analysis.setMatchingPreferredSkills(new ArrayList<>(matchingPreferredSkills));
        analysis.setMissingRequiredSkills(new ArrayList<>(missingRequiredSkills));
        analysis.setMissingPreferredSkills(new ArrayList<>(missingPreferredSkills));
        analysis.setRequiredMatchPercentage(Math.round(requiredMatchPercentage * 100.0) / 100.0);
        analysis.setPreferredMatchPercentage(Math.round(preferredMatchPercentage * 100.0) / 100.0);
        analysis.setOverallMatchPercentage(Math.round(overallMatchPercentage * 100.0) / 100.0);
        analysis.setExperienceMatch(experienceMatch);
        analysis.setEducationMatch(educationMatch);
        analysis.setImprovementSuggestions(suggestions);
        analysis.setOverallFit(calculateOverallFit(overallMatchPercentage, experienceMatch, educationMatch));
        
        return analysis;
    }

    private Set<String> extractAllResumeSkills(ResumeData resume) {
        Set<String> skills = new HashSet<>();
        
        // Add explicit skills
        if (resume.getSkills() != null) {
            skills.addAll(resume.getSkills().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet()));
        }
        
        // Extract skills from experience
        if (resume.getExperience() != null) {
            for (ResumeData.Experience exp : resume.getExperience()) {
                if (exp.getResponsibilities() != null) {
                    for (String responsibility : exp.getResponsibilities()) {
                        skills.addAll(extractSkillsFromText(responsibility.toLowerCase()));
                    }
                }
            }
        }
        
        // Extract skills from projects
        if (resume.getProjects() != null) {
            for (ResumeData.Project project : resume.getProjects()) {
                if (project.getTechnologies() != null) {
                    skills.addAll(project.getTechnologies().stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet()));
                }
                if (project.getDescription() != null) {
                    skills.addAll(extractSkillsFromText(project.getDescription().toLowerCase()));
                }
            }
        }
        
        return skills;
    }

    private Set<String> extractSkillsFromText(String text) {
        Set<String> skills = new HashSet<>();
        
        // Common technology keywords to look for
        String[] techKeywords = {
            "java", "python", "javascript", "typescript", "react", "angular", "vue", "node", "spring",
            "docker", "kubernetes", "aws", "azure", "gcp", "mysql", "postgresql", "mongodb", "redis",
            "git", "jenkins", "maven", "gradle", "rest", "api", "microservices", "agile", "scrum",
            "html", "css", "sql", "nosql", "linux", "ubuntu", "windows", "macos", "gitlab", "github"
        };
        
        for (String keyword : techKeywords) {
            if (text.contains(keyword)) {
                skills.add(keyword);
            }
        }
        
        return skills;
    }

    private Set<String> findMatchingSkills(Set<String> resumeSkills, Set<String> jobSkills) {
        Set<String> matchingSkills = new HashSet<>();
        
        for (String jobSkill : jobSkills) {
            String normalizedJobSkill = jobSkill.toLowerCase().trim();
            
            // Direct match
            if (resumeSkills.contains(normalizedJobSkill)) {
                matchingSkills.add(jobSkill);
                continue;
            }
            
            // Partial match for compound skills
            for (String resumeSkill : resumeSkills) {
                if (resumeSkill.contains(normalizedJobSkill) || normalizedJobSkill.contains(resumeSkill)) {
                    matchingSkills.add(jobSkill);
                    break;
                }
            }
        }
        
        return matchingSkills;
    }

    private ExperienceMatch analyzeExperienceMatch(ResumeData resume, JobDescription jobDescription) {
        ExperienceMatch match = new ExperienceMatch();
        
        // Calculate total years of experience from resume
        int totalYears = calculateTotalExperience(resume);
        
        // Extract required years from job description
        int requiredYears = extractRequiredYears(jobDescription.getExperienceLevel());
        
        match.setResumeYears(totalYears);
        match.setRequiredYears(requiredYears);
        match.setMeetsRequirement(totalYears >= requiredYears);
        match.setMatchDescription(totalYears >= requiredYears ? 
            String.format("Meets requirement (%d years vs %d required)", totalYears, requiredYears) :
            String.format("Below requirement (%d years vs %d required)", totalYears, requiredYears));
        
        return match;
    }

    private int calculateTotalExperience(ResumeData resume) {
        int totalYears = 0;
        
        if (resume.getExperience() != null) {
            for (ResumeData.Experience exp : resume.getExperience()) {
                if (exp.getDuration() != null) {
                    totalYears += extractYearsFromDuration(exp.getDuration());
                }
            }
        }
        
        return totalYears;
    }

    private int extractYearsFromDuration(String duration) {
        if (duration == null) return 0;
        
        // Look for patterns like "3 years", "2-3 years", "2019-2022", etc.
        try {
            if (duration.toLowerCase().contains("year")) {
                String[] parts = duration.toLowerCase().split("year");
                String yearPart = parts[0].trim();
                if (yearPart.contains("-")) {
                    String[] range = yearPart.split("-");
                    return Integer.parseInt(range[range.length - 1].trim());
                } else {
                    return Integer.parseInt(yearPart.replaceAll("[^0-9]", ""));
                }
            } else if (duration.matches(".*\\d{4}.*")) {
                // Extract years from date ranges like "2019-2022"
                String[] years = duration.replaceAll("[^0-9\\s-]", "").split("-");
                if (years.length >= 2) {
                    return Integer.parseInt(years[years.length - 1].trim()) - Integer.parseInt(years[0].trim());
                }
            }
        } catch (Exception e) {
            // Return 0 if parsing fails
        }
        
        return 0;
    }

    private int extractRequiredYears(String experienceLevel) {
        if (experienceLevel == null) return 0;
        
        String lower = experienceLevel.toLowerCase();
        
        if (lower.contains("entry") || lower.contains("junior") || lower.contains("0-2")) {
            return 0;
        } else if (lower.contains("mid") || lower.contains("intermediate") || lower.contains("3-5")) {
            return 3;
        } else if (lower.contains("senior") || lower.contains("5+") || lower.contains("lead")) {
            return 5;
        } else if (lower.contains("manager") || lower.contains("director")) {
            return 7;
        }
        
        // Try to extract number from text
        try {
            String[] parts = lower.split("\\s+");
            for (String part : parts) {
                if (part.matches("\\d+")) {
                    return Integer.parseInt(part);
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        
        return 0;
    }

    private EducationMatch analyzeEducationMatch(ResumeData resume, JobDescription jobDescription) {
        EducationMatch match = new EducationMatch();
        
        boolean hasBachelor = false;
        boolean hasMaster = false;
        boolean hasPhD = false;
        
        if (resume.getEducation() != null) {
            for (ResumeData.Education edu : resume.getEducation()) {
                if (edu.getDegree() != null) {
                    String degree = edu.getDegree().toLowerCase();
                    if (degree.contains("bachelor")) hasBachelor = true;
                    if (degree.contains("master")) hasMaster = true;
                    if (degree.contains("phd") || degree.contains("doctor")) hasPhD = true;
                }
            }
        }
        
        match.setHasBachelor(hasBachelor);
        match.setHasMaster(hasMaster);
        match.setHasPhD(hasPhD);
        
        // Check if education requirements are met
        String requirements = jobDescription.getDescription() != null ? 
            jobDescription.getDescription().toLowerCase() : "";
        
        if (requirements.contains("bachelor") || requirements.contains("degree")) {
            match.setMeetsRequirement(hasBachelor);
            match.setMatchDescription(hasBachelor ? "Bachelor's degree requirement met" : 
                "Bachelor's degree required but not found");
        } else if (requirements.contains("master")) {
            match.setMeetsRequirement(hasMaster);
            match.setMatchDescription(hasMaster ? "Master's degree requirement met" : 
                "Master's degree required but not found");
        } else {
            match.setMeetsRequirement(true);
            match.setMatchDescription("No specific education requirement found");
        }
        
        return match;
    }

    private List<String> generateImprovementSuggestions(Set<String> missingRequired, Set<String> missingPreferred,
                                                       ExperienceMatch expMatch, EducationMatch eduMatch) {
        List<String> suggestions = new ArrayList<>();
        
        // Skill suggestions
        if (!missingRequired.isEmpty()) {
            suggestions.add("Focus on learning these required skills: " + String.join(", ", missingRequired));
        }
        
        if (!missingPreferred.isEmpty()) {
            suggestions.add("Consider learning these preferred skills to stand out: " + String.join(", ", missingPreferred));
        }
        
        // Experience suggestions
        if (!expMatch.isMeetsRequirement()) {
            suggestions.add(String.format("Gain %d more years of relevant experience to meet requirements", 
                expMatch.getRequiredYears() - expMatch.getResumeYears()));
        }
        
        // Education suggestions
        if (!eduMatch.isMeetsRequirement()) {
            suggestions.add("Consider pursuing relevant education to meet degree requirements");
        }
        
        // General suggestions
        suggestions.add("Quantify your achievements with metrics and numbers");
        suggestions.add("Use strong action verbs in your bullet points");
        suggestions.add("Tailor your resume summary to match the job description");
        suggestions.add("Highlight projects that demonstrate the required skills");
        
        return suggestions;
    }

    private String calculateOverallFit(double matchPercentage, ExperienceMatch expMatch, EducationMatch eduMatch) {
        double score = matchPercentage;
        
        if (expMatch.isMeetsRequirement()) {
            score += 10;
        } else {
            score -= 15;
        }
        
        if (eduMatch.isMeetsRequirement()) {
            score += 5;
        } else {
            score -= 10;
        }
        
        if (score >= 80) return "Excellent Fit";
        if (score >= 60) return "Good Fit";
        if (score >= 40) return "Moderate Fit";
        if (score >= 20) return "Low Fit";
        return "Poor Fit";
    }

    public static class SkillGapAnalysis {
        private List<String> resumeSkills;
        private List<String> matchingRequiredSkills;
        private List<String> matchingPreferredSkills;
        private List<String> missingRequiredSkills;
        private List<String> missingPreferredSkills;
        private double requiredMatchPercentage;
        private double preferredMatchPercentage;
        private double overallMatchPercentage;
        private ExperienceMatch experienceMatch;
        private EducationMatch educationMatch;
        private List<String> improvementSuggestions;
        private String overallFit;

        // Getters and Setters
        public List<String> getResumeSkills() { return resumeSkills; }
        public void setResumeSkills(List<String> resumeSkills) { this.resumeSkills = resumeSkills; }

        public List<String> getMatchingRequiredSkills() { return matchingRequiredSkills; }
        public void setMatchingRequiredSkills(List<String> matchingRequiredSkills) { this.matchingRequiredSkills = matchingRequiredSkills; }

        public List<String> getMatchingPreferredSkills() { return matchingPreferredSkills; }
        public void setMatchingPreferredSkills(List<String> matchingPreferredSkills) { this.matchingPreferredSkills = matchingPreferredSkills; }

        public List<String> getMissingRequiredSkills() { return missingRequiredSkills; }
        public void setMissingRequiredSkills(List<String> missingRequiredSkills) { this.missingRequiredSkills = missingRequiredSkills; }

        public List<String> getMissingPreferredSkills() { return missingPreferredSkills; }
        public void setMissingPreferredSkills(List<String> missingPreferredSkills) { this.missingPreferredSkills = missingPreferredSkills; }

        public double getRequiredMatchPercentage() { return requiredMatchPercentage; }
        public void setRequiredMatchPercentage(double requiredMatchPercentage) { this.requiredMatchPercentage = requiredMatchPercentage; }

        public double getPreferredMatchPercentage() { return preferredMatchPercentage; }
        public void setPreferredMatchPercentage(double preferredMatchPercentage) { this.preferredMatchPercentage = preferredMatchPercentage; }

        public double getOverallMatchPercentage() { return overallMatchPercentage; }
        public void setOverallMatchPercentage(double overallMatchPercentage) { this.overallMatchPercentage = overallMatchPercentage; }

        public ExperienceMatch getExperienceMatch() { return experienceMatch; }
        public void setExperienceMatch(ExperienceMatch experienceMatch) { this.experienceMatch = experienceMatch; }

        public EducationMatch getEducationMatch() { return educationMatch; }
        public void setEducationMatch(EducationMatch educationMatch) { this.educationMatch = educationMatch; }

        public List<String> getImprovementSuggestions() { return improvementSuggestions; }
        public void setImprovementSuggestions(List<String> improvementSuggestions) { this.improvementSuggestions = improvementSuggestions; }

        public String getOverallFit() { return overallFit; }
        public void setOverallFit(String overallFit) { this.overallFit = overallFit; }
    }

    public static class ExperienceMatch {
        private int resumeYears;
        private int requiredYears;
        private boolean meetsRequirement;
        private String matchDescription;

        // Getters and Setters
        public int getResumeYears() { return resumeYears; }
        public void setResumeYears(int resumeYears) { this.resumeYears = resumeYears; }

        public int getRequiredYears() { return requiredYears; }
        public void setRequiredYears(int requiredYears) { this.requiredYears = requiredYears; }

        public boolean isMeetsRequirement() { return meetsRequirement; }
        public void setMeetsRequirement(boolean meetsRequirement) { this.meetsRequirement = meetsRequirement; }

        public String getMatchDescription() { return matchDescription; }
        public void setMatchDescription(String matchDescription) { this.matchDescription = matchDescription; }
    }

    public static class EducationMatch {
        private boolean hasBachelor;
        private boolean hasMaster;
        private boolean hasPhD;
        private boolean meetsRequirement;
        private String matchDescription;

        // Getters and Setters
        public boolean isHasBachelor() { return hasBachelor; }
        public void setHasBachelor(boolean hasBachelor) { this.hasBachelor = hasBachelor; }

        public boolean isHasMaster() { return hasMaster; }
        public void setHasMaster(boolean hasMaster) { this.hasMaster = hasMaster; }

        public boolean isHasPhD() { return hasPhD; }
        public void setHasPhD(boolean hasPhD) { this.hasPhD = hasPhD; }

        public boolean isMeetsRequirement() { return meetsRequirement; }
        public void setMeetsRequirement(boolean meetsRequirement) { this.meetsRequirement = meetsRequirement; }

        public String getMatchDescription() { return matchDescription; }
        public void setMatchDescription(String matchDescription) { this.matchDescription = matchDescription; }
    }
}
