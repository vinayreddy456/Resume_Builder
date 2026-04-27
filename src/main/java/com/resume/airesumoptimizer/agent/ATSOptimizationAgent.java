package com.resume.airesumoptimizer.agent;

import com.resume.airesumoptimizer.model.ResumeData;
import com.resume.airesumoptimizer.model.JobDescription;
import com.resume.airesumoptimizer.agent.ResumeRewriterAgent.OptimizedResume;
import com.resume.airesumoptimizer.agent.ResumeRewriterAgent.OptimizedResume.OptimizedExperience;
import com.resume.airesumoptimizer.agent.ResumeRewriterAgent.OptimizedResume.OptimizedProject;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ATSOptimizationAgent {

    private static final Pattern SPECIAL_CHARACTERS = Pattern.compile("[^a-zA-Z0-9\\s\\-.,]");
    private static final int MAX_KEYWORD_DENSITY = 3; // Maximum times a keyword should appear
    private static final int MIN_KEYWORD_DENSITY = 1; // Minimum times a keyword should appear

    public ATSOptimizedResume optimizeForATS(OptimizedResume optimizedResume, JobDescription jobDescription) {
        ATSOptimizedResume atsResume = new ATSOptimizedResume();
        
        // Optimize each section for ATS
        atsResume.setSummary(optimizeSummaryForATS(optimizedResume.getSummary(), jobDescription));
        atsResume.setSkills(optimizeSkillsForATS(optimizedResume.getSkills(), jobDescription));
        atsResume.setExperience(optimizeExperienceForATS(optimizedResume.getExperience(), jobDescription));
        atsResume.setProjects(optimizeProjectsForATS(optimizedResume.getProjects(), jobDescription));
        atsResume.setEducation(optimizedResume.getEducation());
        atsResume.setCertifications(optimizedResume.getCertifications());
        
        // Add ATS-specific metadata
        atsResume.setKeywordDensity(calculateKeywordDensity(atsResume, jobDescription));
        atsResume.setAtsScore(calculateATSScore(atsResume, jobDescription));
        atsResume.setOptimizationSuggestions(generateATSOptimizationSuggestions(atsResume, jobDescription));
        
        return atsResume;
    }

    private String optimizeSummaryForATS(String summary, JobDescription jobDescription) {
        if (summary == null) return null;
        
        // Clean special characters
        String cleanSummary = cleanTextForATS(summary);
        
        // Ensure job title is present
        String jobTitle = jobDescription.getJobTitle();
        if (jobTitle != null && !cleanSummary.toLowerCase().contains(jobTitle.toLowerCase())) {
            cleanSummary = jobTitle + " with " + cleanSummary;
        }
        
        // Add key skills naturally
        List<String> requiredSkills = jobDescription.getRequiredSkills();
        if (requiredSkills != null && !requiredSkills.isEmpty()) {
            cleanSummary = insertKeywordsNaturally(cleanSummary, requiredSkills.subList(0, Math.min(3, requiredSkills.size())));
        }
        
        return cleanSummary;
    }

    private Map<String, List<String>> optimizeSkillsForATS(Map<String, List<String>> skills, 
                                                          JobDescription jobDescription) {
        Map<String, List<String>> optimizedSkills = new LinkedHashMap<>();
        
        // Ensure all required skills are included
        Set<String> allRequiredSkills = new HashSet<>();
        if (jobDescription.getRequiredSkills() != null) {
            allRequiredSkills.addAll(jobDescription.getRequiredSkills().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet()));
        }
        
        // Add preferred skills
        Set<String> allPreferredSkills = new HashSet<>();
        if (jobDescription.getPreferredSkills() != null) {
            allPreferredSkills.addAll(jobDescription.getPreferredSkills().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet()));
        }
        
        // Process existing skills and ensure they're ATS-friendly
        for (Map.Entry<String, List<String>> entry : skills.entrySet()) {
            String category = entry.getKey();
            List<String> skillList = entry.getValue();
            
            // Clean skill names
            List<String> cleanedSkills = skillList.stream()
                .map(this::cleanTextForATS)
                .map(String::toLowerCase)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
            
            // Add any missing required skills to appropriate categories
            addMissingSkillsToCategory(cleanedSkills, allRequiredSkills, category);
            
            if (!cleanedSkills.isEmpty()) {
                optimizedSkills.put(category, cleanedSkills);
            }
        }
        
        // Add any remaining required skills to "Technical Skills" category
        if (!allRequiredSkills.isEmpty()) {
            String technicalCategory = "Technical Skills";
            if (!optimizedSkills.containsKey(technicalCategory)) {
                optimizedSkills.put(technicalCategory, new ArrayList<>());
            }
            optimizedSkills.get(technicalCategory).addAll(allRequiredSkills);
            Collections.sort(optimizedSkills.get(technicalCategory));
        }
        
        return optimizedSkills;
    }

    private void addMissingSkillsToCategory(List<String> existingSkills, Set<String> requiredSkills, String category) {
        String categoryLower = category.toLowerCase();
        Iterator<String> iterator = requiredSkills.iterator();
        
        while (iterator.hasNext()) {
            String skill = iterator.next();
            boolean added = false;
            
            // Check if skill fits in current category
            if (categoryLower.contains("language") && isProgrammingLanguage(skill)) {
                if (!existingSkills.contains(skill)) {
                    existingSkills.add(skill);
                }
                iterator.remove();
                added = true;
            } else if (categoryLower.contains("framework") && isFramework(skill)) {
                if (!existingSkills.contains(skill)) {
                    existingSkills.add(skill);
                }
                iterator.remove();
                added = true;
            } else if (categoryLower.contains("database") && isDatabase(skill)) {
                if (!existingSkills.contains(skill)) {
                    existingSkills.add(skill);
                }
                iterator.remove();
                added = true;
            } else if (categoryLower.contains("cloud") && isCloudPlatform(skill)) {
                if (!existingSkills.contains(skill)) {
                    existingSkills.add(skill);
                }
                iterator.remove();
                added = true;
            }
        }
    }

    private List<ATSOptimizedResume.ATSOptimizedExperience> optimizeExperienceForATS(
            List<OptimizedExperience> experience, JobDescription jobDescription) {
        
        if (experience == null) return new ArrayList<>();
        
        return experience.stream().map((OptimizedExperience exp) -> {
            ATSOptimizedResume.ATSOptimizedExperience atsExp = new ATSOptimizedResume.ATSOptimizedExperience();
            atsExp.setCompany(exp.getCompany());
            atsExp.setPosition(exp.getPosition());
            atsExp.setDuration(exp.getDuration());
            atsExp.setLocation(exp.getLocation());
            
            // Optimize responsibilities for ATS
            List<String> optimizedResponsibilities = new ArrayList<>();
            if (exp.getResponsibilities() != null) {
                for (String responsibility : exp.getResponsibilities()) {
                    String optimized = optimizeResponsibilityForATS(responsibility, jobDescription);
                    optimizedResponsibilities.add(optimized);
                }
            }
            atsExp.setResponsibilities(optimizedResponsibilities);
            
            return atsExp;
        }).collect(Collectors.toList());
    }

    private String optimizeResponsibilityForATS(String responsibility, JobDescription jobDescription) {
        if (responsibility == null) return null;
        
        // Clean special characters
        String clean = cleanTextForATS(responsibility);
        
        // Ensure it starts with action verb
        if (!startsWithActionVerb(clean)) {
            clean = "Developed " + clean;
        }
        
        // Add relevant keywords if missing
        List<String> requiredSkills = jobDescription.getRequiredSkills();
        if (requiredSkills != null && !requiredSkills.isEmpty()) {
            clean = insertKeywordsNaturally(clean, requiredSkills.subList(0, Math.min(2, requiredSkills.size())));
        }
        
        // Ensure it has metrics
        if (!containsMetrics(clean)) {
            clean = addATSMetrics(clean);
        }
        
        return clean;
    }

    private List<ATSOptimizedResume.ATSOptimizedProject> optimizeProjectsForATS(
            List<OptimizedProject> projects, JobDescription jobDescription) {
        
        if (projects == null) return new ArrayList<>();
        
        return projects.stream().map((OptimizedProject project) -> {
            ATSOptimizedResume.ATSOptimizedProject atsProject = new ATSOptimizedResume.ATSOptimizedProject();
            atsProject.setName(project.getName());
            atsProject.setDescription(cleanTextForATS(project.getDescription()));
            
            // Clean and optimize technologies
            List<String> optimizedTech = new ArrayList<>();
            if (project.getTechnologies() != null) {
                optimizedTech = project.getTechnologies().stream()
                    .map(this::cleanTextForATS)
                    .map(String::toLowerCase)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            }
            atsProject.setTechnologies(optimizedTech);
            
            // Optimize achievements
            List<String> optimizedAchievements = new ArrayList<>();
            if (project.getAchievements() != null) {
                for (String achievement : project.getAchievements()) {
                    String optimized = optimizeResponsibilityForATS(achievement, jobDescription);
                    optimizedAchievements.add(optimized);
                }
            }
            atsProject.setAchievements(optimizedAchievements);
            
            return atsProject;
        }).collect(Collectors.toList());
    }

    private String cleanTextForATS(String text) {
        if (text == null) return null;
        
        // Remove special characters that might confuse ATS
        String cleaned = SPECIAL_CHARACTERS.matcher(text).replaceAll(" ");
        
        // Normalize whitespace
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        
        return cleaned;
    }

    private String insertKeywordsNaturally(String text, List<String> keywords) {
        String result = text;
        int inserted = 0;
        
        for (String keyword : keywords) {
            if (inserted >= 2) break; // Don't overstuff
            
            String lowerKeyword = keyword.toLowerCase();
            String lowerResult = result.toLowerCase();
            
            if (!lowerResult.contains(lowerKeyword)) {
                // Find natural insertion points
                if (result.contains("using") && !result.contains("using " + keyword)) {
                    result = result.replaceFirst("using", "using " + keyword + " and");
                    inserted++;
                } else if (result.contains("with") && !result.contains("with " + keyword)) {
                    result = result.replaceFirst("with", "with " + keyword + " and");
                    inserted++;
                } else if (result.endsWith(".")) {
                    result = result.substring(0, result.length() - 1) + " using " + keyword + ".";
                    inserted++;
                }
            }
        }
        
        return result;
    }

    private boolean startsWithActionVerb(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        
        String[] actionVerbs = {
            "achieved", "implemented", "developed", "led", "managed", "optimized", "designed",
            "built", "created", "improved", "enhanced", "streamlined", "automated", "integrated",
            "deployed", "scaled", "reduced", "increased", "launched", "mentored", "coordinated",
            "architected", "refactored", "modernized", "spearheaded", "oversaw", "delivered"
        };
        
        String firstWord = text.split("\\s+")[0].toLowerCase();
        return Arrays.asList(actionVerbs).contains(firstWord);
    }

    private boolean containsMetrics(String text) {
        return text.matches(".*\\d+.*%.*") || text.matches(".*\\d+.*") ||
               text.toLowerCase().contains("reduced") || text.toLowerCase().contains("increased") ||
               text.toLowerCase().contains("improved") || text.toLowerCase().contains("optimized");
    }

    private String addATSMetrics(String text) {
        String lowerText = text.toLowerCase();
        
        if (lowerText.contains("performance") || lowerText.contains("optimization")) {
            return text + " resulting in 25% performance improvement";
        } else if (lowerText.contains("development") || lowerText.contains("built")) {
            return text + " deployed to production serving 5000+ users";
        } else if (lowerText.contains("team") || lowerText.contains("led")) {
            return text + " improving team productivity by 20%";
        } else {
            return text + " delivering measurable business impact";
        }
    }

    private Map<String, Integer> calculateKeywordDensity(ATSOptimizedResume resume, JobDescription jobDescription) {
        Map<String, Integer> keywordDensity = new HashMap<>();
        
        // Combine all text from resume
        StringBuilder allText = new StringBuilder();
        allText.append(resume.getSummary()).append(" ");
        
        if (resume.getSkills() != null) {
            resume.getSkills().values().forEach(skills -> 
                allText.append(String.join(" ", skills)).append(" "));
        }
        
        if (resume.getExperience() != null) {
            resume.getExperience().forEach(exp -> {
                if (exp.getResponsibilities() != null) {
                    allText.append(String.join(" ", exp.getResponsibilities())).append(" ");
                }
            });
        }
        
        if (resume.getProjects() != null) {
            resume.getProjects().forEach(project -> {
                allText.append(project.getName()).append(" ");
                allText.append(project.getDescription()).append(" ");
                if (project.getTechnologies() != null) {
                    allText.append(String.join(" ", project.getTechnologies())).append(" ");
                }
            });
        }
        
        String resumeText = allText.toString().toLowerCase();
        
        // Count keyword occurrences
        List<String> allJobKeywords = new ArrayList<>();
        if (jobDescription.getRequiredSkills() != null) {
            allJobKeywords.addAll(jobDescription.getRequiredSkills());
        }
        if (jobDescription.getPreferredSkills() != null) {
            allJobKeywords.addAll(jobDescription.getPreferredSkills());
        }
        
        for (String keyword : allJobKeywords) {
            String lowerKeyword = keyword.toLowerCase();
            int count = countOccurrences(resumeText, lowerKeyword);
            keywordDensity.put(keyword, count);
        }
        
        return keywordDensity;
    }

    private int countOccurrences(String text, String keyword) {
        int count = 0;
        int index = 0;
        
        while ((index = text.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        
        return count;
    }

    private int calculateATSScore(ATSOptimizedResume resume, JobDescription jobDescription) {
        int score = 0;
        int totalKeywords = 0;
        int matchedKeywords = 0;
        
        // Count total keywords from job description
        if (jobDescription.getRequiredSkills() != null) {
            totalKeywords += jobDescription.getRequiredSkills().size();
        }
        if (jobDescription.getPreferredSkills() != null) {
            totalKeywords += jobDescription.getPreferredSkills().size();
        }
        
        // Count matched keywords
        Map<String, Integer> keywordDensity = resume.getKeywordDensity();
        for (Map.Entry<String, Integer> entry : keywordDensity.entrySet()) {
            if (entry.getValue() > 0) {
                matchedKeywords++;
                score += Math.min(entry.getValue() * 10, 30); // Max 30 points per keyword
            }
        }
        
        // Bonus for keyword density optimization
        int optimalDensityCount = 0;
        for (int density : keywordDensity.values()) {
            if (density >= MIN_KEYWORD_DENSITY && density <= MAX_KEYWORD_DENSITY) {
                optimalDensityCount++;
            }
        }
        score += optimalDensityCount * 5;
        
        // Bonus for proper formatting
        if (resume.getSummary() != null && resume.getSummary().length() > 50) score += 10;
        if (resume.getSkills() != null && !resume.getSkills().isEmpty()) score += 10;
        if (resume.getExperience() != null && !resume.getExperience().isEmpty()) score += 10;
        
        return Math.min(score, 100); // Cap at 100
    }

    private List<String> generateATSOptimizationSuggestions(ATSOptimizedResume resume, JobDescription jobDescription) {
        List<String> suggestions = new ArrayList<>();
        
        Map<String, Integer> keywordDensity = resume.getKeywordDensity();
        
        // Check for missing keywords
        List<String> allJobKeywords = new ArrayList<>();
        if (jobDescription.getRequiredSkills() != null) {
            allJobKeywords.addAll(jobDescription.getRequiredSkills());
        }
        if (jobDescription.getPreferredSkills() != null) {
            allJobKeywords.addAll(jobDescription.getPreferredSkills());
        }
        
        for (String keyword : allJobKeywords) {
            Integer density = keywordDensity.get(keyword);
            if (density == null || density == 0) {
                suggestions.add("Add keyword '" + keyword + "' to your resume");
            } else if (density > MAX_KEYWORD_DENSITY) {
                suggestions.add("Reduce keyword '" + keyword + "' density (currently " + density + " times)");
            }
        }
        
        // Check ATS score
        if (resume.getAtsScore() < 70) {
            suggestions.add("Overall ATS score is low. Consider adding more relevant keywords and metrics");
        }
        
        // Formatting suggestions
        if (resume.getSummary() == null || resume.getSummary().length() < 50) {
            suggestions.add("Expand your summary to include more keywords and achievements");
        }
        
        return suggestions;
    }

    // Helper methods for skill categorization
    private boolean isProgrammingLanguage(String skill) {
        List<String> languages = Arrays.asList("java", "python", "javascript", "typescript", "c++", "c#", 
            "go", "rust", "swift", "kotlin", "scala", "php", "ruby");
        return languages.contains(skill.toLowerCase());
    }

    private boolean isFramework(String skill) {
        List<String> frameworks = Arrays.asList("spring boot", "spring", "react", "angular", "vue", 
            "django", "flask", "express", "node.js", "dotnet", "laravel", "rails");
        return frameworks.contains(skill.toLowerCase());
    }

    private boolean isDatabase(String skill) {
        List<String> databases = Arrays.asList("mysql", "postgresql", "mongodb", "oracle", 
            "sql server", "redis", "elasticsearch", "cassandra", "dynamodb");
        return databases.contains(skill.toLowerCase());
    }

    private boolean isCloudPlatform(String skill) {
        List<String> platforms = Arrays.asList("aws", "azure", "google cloud", "gcp", 
            "heroku", "digitalocean", "vercel", "netlify");
        return platforms.contains(skill.toLowerCase());
    }

    public static class ATSOptimizedResume {
        private String summary;
        private Map<String, List<String>> skills;
        private List<ATSOptimizedExperience> experience;
        private List<ATSOptimizedProject> projects;
        private List<ResumeData.Education> education;
        private List<String> certifications;
        private Map<String, Integer> keywordDensity;
        private int atsScore;
        private List<String> optimizationSuggestions;

        // Getters and Setters
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }

        public Map<String, List<String>> getSkills() { return skills; }
        public void setSkills(Map<String, List<String>> skills) { this.skills = skills; }

        public List<ATSOptimizedExperience> getExperience() { return experience; }
        public void setExperience(List<ATSOptimizedExperience> experience) { this.experience = experience; }

        public List<ATSOptimizedProject> getProjects() { return projects; }
        public void setProjects(List<ATSOptimizedProject> projects) { this.projects = projects; }

        public List<ResumeData.Education> getEducation() { return education; }
        public void setEducation(List<ResumeData.Education> education) { this.education = education; }

        public List<String> getCertifications() { return certifications; }
        public void setCertifications(List<String> certifications) { this.certifications = certifications; }

        public Map<String, Integer> getKeywordDensity() { return keywordDensity; }
        public void setKeywordDensity(Map<String, Integer> keywordDensity) { this.keywordDensity = keywordDensity; }

        public int getAtsScore() { return atsScore; }
        public void setAtsScore(int atsScore) { this.atsScore = atsScore; }

        public List<String> getOptimizationSuggestions() { return optimizationSuggestions; }
        public void setOptimizationSuggestions(List<String> optimizationSuggestions) { this.optimizationSuggestions = optimizationSuggestions; }

        public static class ATSOptimizedExperience {
            private String company;
            private String position;
            private String duration;
            private String location;
            private List<String> responsibilities;

            // Getters and Setters
            public String getCompany() { return company; }
            public void setCompany(String company) { this.company = company; }

            public String getPosition() { return position; }
            public void setPosition(String position) { this.position = position; }

            public String getDuration() { return duration; }
            public void setDuration(String duration) { this.duration = duration; }

            public String getLocation() { return location; }
            public void setLocation(String location) { this.location = location; }

            public List<String> getResponsibilities() { return responsibilities; }
            public void setResponsibilities(List<String> responsibilities) { this.responsibilities = responsibilities; }
        }

        public static class ATSOptimizedProject {
            private String name;
            private String description;
            private List<String> technologies;
            private List<String> achievements;

            // Getters and Setters
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }

            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }

            public List<String> getTechnologies() { return technologies; }
            public void setTechnologies(List<String> technologies) { this.technologies = technologies; }

            public List<String> getAchievements() { return achievements; }
            public void setAchievements(List<String> achievements) { this.achievements = achievements; }
        }
    }
}
