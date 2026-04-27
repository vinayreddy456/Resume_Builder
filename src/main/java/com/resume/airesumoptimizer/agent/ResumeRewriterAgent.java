package com.resume.airesumoptimizer.agent;

import com.resume.airesumoptimizer.model.ResumeData;
import com.resume.airesumoptimizer.model.JobDescription;
import com.resume.airesumoptimizer.agent.SkillGapAnalyzerAgent.SkillGapAnalysis;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ResumeRewriterAgent {

    private static final List<String> ACTION_VERBS = Arrays.asList(
        "Achieved", "Implemented", "Developed", "Led", "Managed", "Optimized", "Designed", 
        "Built", "Created", "Improved", "Enhanced", "Streamlined", "Automated", "Integrated", 
        "Deployed", "Scaled", "Reduced", "Increased", "Launched", "Mentored", "Coordinated", 
        "Architected", "Refactored", "Modernized", "Spearheaded", "Oversaw", "Delivered"
    );

    public OptimizedResume rewriteResume(ResumeData originalResume, JobDescription jobDescription, 
                                        SkillGapAnalysis skillGapAnalysis) {
        OptimizedResume optimized = new OptimizedResume();
        
        // Create optimized summary
        optimized.setSummary(createOptimizedSummary(originalResume, jobDescription, skillGapAnalysis));
        
        // Optimize skills section
        optimized.setSkills(optimizeSkillsSection(originalResume, jobDescription, skillGapAnalysis));
        
        // Optimize experience section
        optimized.setExperience(optimizeExperienceSection(originalResume, jobDescription, skillGapAnalysis));
        
        // Optimize projects section
        optimized.setProjects(optimizeProjectsSection(originalResume, jobDescription, skillGapAnalysis));
        
        // Keep education as-is but format it
        optimized.setEducation(formatEducationSection(originalResume));
        
        // Add additional sections
        optimized.setCertifications(originalResume.getCertifications());
        
        return optimized;
    }

    private String createOptimizedSummary(ResumeData resume, JobDescription jobDescription, 
                                        SkillGapAnalysis skillGapAnalysis) {
        StringBuilder summary = new StringBuilder();
        
        // Start with professional title matching the job
        String jobTitle = jobDescription.getJobTitle();
        if (jobTitle != null) {
            summary.append(String.format("Results-oriented %s with ", jobTitle.toLowerCase()));
        } else {
            summary.append("Results-oriented professional with ");
        }
        
        // Add years of experience
        int totalYears = skillGapAnalysis.getExperienceMatch().getResumeYears();
        summary.append(totalYears).append("+ years of experience");
        
        // Add key skills that match the job requirements
        List<String> topMatchingSkills = skillGapAnalysis.getMatchingRequiredSkills().stream()
            .limit(5)
            .collect(Collectors.toList());
        
        if (!topMatchingSkills.isEmpty()) {
            summary.append(" in ").append(String.join(", ", topMatchingSkills));
        }
        
        // Add achievement statement
        summary.append(". Proven track record of ");
        
        // Add relevant achievements based on job type
        if (jobDescription.getJobTitle() != null) {
            String jobTitleLower = jobDescription.getJobTitle().toLowerCase();
            if (jobTitleLower.contains("backend") || jobTitleLower.contains("java") || 
                jobTitleLower.contains("spring")) {
                summary.append("building scalable backend systems and optimizing application performance");
            } else if (jobTitleLower.contains("full stack") || jobTitleLower.contains("frontend")) {
                summary.append("developing responsive web applications and enhancing user experience");
            } else if (jobTitleLower.contains("devops") || jobTitleLower.contains("cloud")) {
                summary.append("implementing CI/CD pipelines and managing cloud infrastructure");
            } else {
                summary.append("delivering high-quality software solutions and driving business impact");
            }
        }
        
        summary.append(". Seeking to leverage technical expertise and leadership skills to contribute to organizational success.");
        
        return summary.toString();
    }

    private Map<String, List<String>> optimizeSkillsSection(ResumeData resume, JobDescription jobDescription, 
                                                           SkillGapAnalysis skillGapAnalysis) {
        Map<String, List<String>> categorizedSkills = new LinkedHashMap<>();
        
        // Get all skills from resume and job
        Set<String> allSkills = new HashSet<>();
        if (resume.getSkills() != null) {
            allSkills.addAll(resume.getSkills().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet()));
        }
        
        // Add matching required and preferred skills
        allSkills.addAll(skillGapAnalysis.getMatchingRequiredSkills().stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet()));
        allSkills.addAll(skillGapAnalysis.getMatchingPreferredSkills().stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet()));
        
        // Categorize skills
        categorizedSkills.put("Programming Languages", 
            filterSkillsByCategory(allSkills, Arrays.asList("java", "python", "javascript", "typescript", 
                "c++", "c#", "go", "rust", "swift", "kotlin", "scala", "php")));
        
        categorizedSkills.put("Frameworks & Libraries", 
            filterSkillsByCategory(allSkills, Arrays.asList("spring boot", "spring", "react", "angular", 
                "vue", "django", "flask", "express", "node.js", "dotnet", "laravel", "rails")));
        
        categorizedSkills.put("Databases", 
            filterSkillsByCategory(allSkills, Arrays.asList("mysql", "postgresql", "mongodb", "oracle", 
                "sql server", "redis", "elasticsearch", "cassandra", "dynamodb")));
        
        categorizedSkills.put("Cloud & DevOps", 
            filterSkillsByCategory(allSkills, Arrays.asList("aws", "azure", "google cloud", "gcp", 
                "docker", "kubernetes", "jenkins", "gitlab", "github actions", "terraform", "ansible")));
        
        categorizedSkills.put("Tools & Technologies", 
            filterSkillsByCategory(allSkills, Arrays.asList("git", "jira", "confluence", "maven", 
                "gradle", "npm", "yarn", "webpack", "postman", "rest", "api", "microservices")));
        
        // Remove empty categories
        return categorizedSkills.entrySet().stream()
            .filter(entry -> !entry.getValue().isEmpty())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private List<String> filterSkillsByCategory(Set<String> allSkills, List<String> categorySkills) {
        return categorySkills.stream()
            .filter(allSkills::contains)
            .sorted()
            .collect(Collectors.toList());
    }

    private List<OptimizedResume.OptimizedExperience> optimizeExperienceSection(ResumeData resume, 
                                                                               JobDescription jobDescription, 
                                                                               SkillGapAnalysis skillGapAnalysis) {
        List<OptimizedResume.OptimizedExperience> optimizedExperience = new ArrayList<>();
        
        if (resume.getExperience() == null) return optimizedExperience;
        
        for (ResumeData.Experience exp : resume.getExperience()) {
            OptimizedResume.OptimizedExperience optExp = new OptimizedResume.OptimizedExperience();
            optExp.setCompany(exp.getCompany());
            optExp.setPosition(exp.getPosition());
            optExp.setDuration(exp.getDuration());
            optExp.setLocation(exp.getLocation());
            
            // Optimize responsibilities
            List<String> optimizedResponsibilities = new ArrayList<>();
            if (exp.getResponsibilities() != null) {
                for (String responsibility : exp.getResponsibilities()) {
                    String optimized = optimizeResponsibility(responsibility, jobDescription, skillGapAnalysis);
                    if (optimized != null) {
                        optimizedResponsibilities.add(optimized);
                    }
                }
            }
            
            // Add more impactful responsibilities if needed
            while (optimizedResponsibilities.size() < 4) {
                String additionalResponsibility = generateAdditionalResponsibility(exp, jobDescription, skillGapAnalysis);
                if (additionalResponsibility != null) {
                    optimizedResponsibilities.add(additionalResponsibility);
                } else {
                    break;
                }
            }
            
            optExp.setResponsibilities(optimizedResponsibilities);
            optimizedExperience.add(optExp);
        }
        
        return optimizedExperience;
    }

    private String optimizeResponsibility(String original, JobDescription jobDescription, 
                                        SkillGapAnalysis skillGapAnalysis) {
        if (original == null || original.trim().isEmpty()) return null;
        
        String optimized = original.trim();
        
        // Add action verb if missing
        if (!startsWithActionVerb(optimized)) {
            optimized = "Developed " + optimized;
        }
        
        // Add metrics if missing
        if (!containsMetrics(optimized)) {
            optimized = addRelevantMetrics(optimized, jobDescription);
        }
        
        // Include relevant keywords from job description
        optimized = includeJobKeywords(optimized, jobDescription, skillGapAnalysis);
        
        // Ensure it starts with capital letter
        if (!optimized.isEmpty()) {
            optimized = Character.toUpperCase(optimized.charAt(0)) + optimized.substring(1);
        }
        
        return optimized;
    }

    private boolean startsWithActionVerb(String text) {
        String firstWord = text.split("\\s+")[0].toLowerCase();
        return ACTION_VERBS.stream().anyMatch(verb -> firstWord.equals(verb.toLowerCase()));
    }

    private boolean containsMetrics(String text) {
        return text.matches(".*\\d+.*%.*") || text.matches(".*\\d+.*") || 
               text.toLowerCase().contains("reduced") || text.toLowerCase().contains("increased") ||
               text.toLowerCase().contains("improved") || text.toLowerCase().contains("optimized");
    }

    private String addRelevantMetrics(String text, JobDescription jobDescription) {
        String jobTitle = jobDescription.getJobTitle();
        if (jobTitle == null) return text;
        
        String lowerText = text.toLowerCase();
        String lowerJobTitle = jobTitle.toLowerCase();
        
        if (lowerText.contains("performance") || lowerText.contains("optimization")) {
            return text + " resulting in 30% improvement in system performance";
        } else if (lowerText.contains("development") || lowerText.contains("built")) {
            return text + " deployed to production serving 10,000+ users";
        } else if (lowerText.contains("team") || lowerText.contains("led")) {
            return text + " mentoring 5 junior developers and improving team productivity by 25%";
        } else if (lowerJobTitle.contains("backend") || lowerJobTitle.contains("java")) {
            return text + " reducing API response time by 40%";
        } else if (lowerJobTitle.contains("frontend") || lowerJobTitle.contains("react")) {
            return text + " improving user engagement by 35%";
        } else {
            return text + " delivering measurable business value and positive user feedback";
        }
    }

    private String includeJobKeywords(String text, JobDescription jobDescription, 
                                    SkillGapAnalysis skillGapAnalysis) {
        List<String> jobKeywords = new ArrayList<>();
        if (jobDescription.getRequiredSkills() != null) {
            jobKeywords.addAll(jobDescription.getRequiredSkills());
        }
        if (jobDescription.getPreferredSkills() != null) {
            jobKeywords.addAll(jobDescription.getPreferredSkills());
        }
        
        String lowerText = text.toLowerCase();
        for (String keyword : jobKeywords) {
            if (!lowerText.contains(keyword.toLowerCase()) && Math.random() > 0.5) {
                // Try to naturally incorporate the keyword
                if (text.contains("using") || text.contains("with")) {
                    text = text.replaceFirst("(using|with)", "$1 " + keyword + " and");
                } else if (text.endsWith(".")) {
                    text = text.substring(0, text.length() - 1) + " using " + keyword + ".";
                } else {
                    text += " utilizing " + keyword;
                }
                break; // Add only one keyword per bullet point
            }
        }
        
        return text;
    }

    private String generateAdditionalResponsibility(ResumeData.Experience exp, JobDescription jobDescription, 
                                                   SkillGapAnalysis skillGapAnalysis) {
        List<String> matchingSkills = skillGapAnalysis.getMatchingRequiredSkills();
        if (matchingSkills.isEmpty()) return null;
        
        String randomSkill = matchingSkills.get(new Random().nextInt(matchingSkills.size()));
        String jobTitle = jobDescription.getJobTitle();
        
        if (jobTitle != null && jobTitle.toLowerCase().contains("backend")) {
            return String.format("Implemented %s-based microservices architecture improving system scalability", randomSkill);
        } else if (jobTitle != null && jobTitle.toLowerCase().contains("frontend")) {
            return String.format("Developed responsive UI components using %s enhancing user experience", randomSkill);
        } else {
            return String.format("Leveraged %s to deliver high-quality software solutions on time and within budget", randomSkill);
        }
    }

    private List<OptimizedResume.OptimizedProject> optimizeProjectsSection(ResumeData resume, 
                                                                         JobDescription jobDescription, 
                                                                         SkillGapAnalysis skillGapAnalysis) {
        List<OptimizedResume.OptimizedProject> optimizedProjects = new ArrayList<>();
        
        if (resume.getProjects() == null) return optimizedProjects;
        
        // Prioritize projects that match job requirements
        List<ResumeData.Project> sortedProjects = resume.getProjects().stream()
            .sorted((p1, p2) -> {
                int score1 = calculateProjectRelevanceScore(p1, jobDescription, skillGapAnalysis);
                int score2 = calculateProjectRelevanceScore(p2, jobDescription, skillGapAnalysis);
                return Integer.compare(score2, score1); // Descending order
            })
            .limit(4) // Keep only top 4 projects
            .collect(Collectors.toList());
        
        for (ResumeData.Project project : sortedProjects) {
            OptimizedResume.OptimizedProject optProject = new OptimizedResume.OptimizedProject();
            optProject.setName(project.getName());
            optProject.setDescription(optimizeProjectDescription(project.getDescription(), jobDescription));
            optProject.setTechnologies(project.getTechnologies());
            
            // Optimize achievements
            List<String> optimizedAchievements = new ArrayList<>();
            if (project.getAchievements() != null) {
                for (String achievement : project.getAchievements()) {
                    String optimized = optimizeResponsibility(achievement, jobDescription, skillGapAnalysis);
                    if (optimized != null) {
                        optimizedAchievements.add(optimized);
                    }
                }
            }
            optProject.setAchievements(optimizedAchievements);
            
            optimizedProjects.add(optProject);
        }
        
        return optimizedProjects;
    }

    private int calculateProjectRelevanceScore(ResumeData.Project project, JobDescription jobDescription, 
                                             SkillGapAnalysis skillGapAnalysis) {
        int score = 0;
        String projectText = (project.getName() + " " + project.getDescription() + " " + 
            (project.getTechnologies() != null ? String.join(" ", project.getTechnologies()) : "")).toLowerCase();
        
        // Check for matching skills
        for (String skill : skillGapAnalysis.getMatchingRequiredSkills()) {
            if (projectText.contains(skill.toLowerCase())) {
                score += 3;
            }
        }
        
        for (String skill : skillGapAnalysis.getMatchingPreferredSkills()) {
            if (projectText.contains(skill.toLowerCase())) {
                score += 1;
            }
        }
        
        // Check for job title keywords
        if (jobDescription.getJobTitle() != null) {
            String jobTitle = jobDescription.getJobTitle().toLowerCase();
            if (jobTitle.contains("backend") && projectText.contains("backend")) score += 2;
            if (jobTitle.contains("frontend") && projectText.contains("frontend")) score += 2;
            if (jobTitle.contains("full stack") && projectText.contains("full stack")) score += 2;
        }
        
        return score;
    }

    private String optimizeProjectDescription(String description, JobDescription jobDescription) {
        if (description == null) return null;
        
        String optimized = description.trim();
        
        // Make it more impactful
        if (!optimized.toLowerCase().startsWith("developed") && 
            !optimized.toLowerCase().startsWith("built") && 
            !optimized.toLowerCase().startsWith("created") &&
            !optimized.toLowerCase().startsWith("implemented")) {
            optimized = "Developed " + optimized;
        }
        
        // Add business impact
        if (!optimized.toLowerCase().contains("impact") && 
            !optimized.toLowerCase().contains("result") &&
            !optimized.toLowerCase().contains("achieve")) {
            optimized += " delivering significant business value";
        }
        
        return optimized;
    }

    private List<ResumeData.Education> formatEducationSection(ResumeData resume) {
        return resume.getEducation(); // Keep as-is for now
    }

    public static class OptimizedResume {
        private String summary;
        private Map<String, List<String>> skills;
        private List<OptimizedExperience> experience;
        private List<OptimizedProject> projects;
        private List<ResumeData.Education> education;
        private List<String> certifications;

        // Getters and Setters
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }

        public Map<String, List<String>> getSkills() { return skills; }
        public void setSkills(Map<String, List<String>> skills) { this.skills = skills; }

        public List<OptimizedExperience> getExperience() { return experience; }
        public void setExperience(List<OptimizedExperience> experience) { this.experience = experience; }

        public List<OptimizedProject> getProjects() { return projects; }
        public void setProjects(List<OptimizedProject> projects) { this.projects = projects; }

        public List<ResumeData.Education> getEducation() { return education; }
        public void setEducation(List<ResumeData.Education> education) { this.education = education; }

        public List<String> getCertifications() { return certifications; }
        public void setCertifications(List<String> certifications) { this.certifications = certifications; }

        public static class OptimizedExperience {
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

        public static class OptimizedProject {
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
