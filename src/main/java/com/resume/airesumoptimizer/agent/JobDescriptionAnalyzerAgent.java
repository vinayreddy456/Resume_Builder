package com.resume.airesumoptimizer.agent;

import com.resume.airesumoptimizer.model.JobDescription;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JobDescriptionAnalyzerAgent {

    // Common skill categories and keywords
    private static final List<String> PROGRAMMING_LANGUAGES = Arrays.asList(
        "java", "python", "javascript", "typescript", "c++", "c#", "ruby", "go", "rust", "swift", "kotlin", "scala", "php"
    );
    
    private static final List<String> FRAMEWORKS = Arrays.asList(
        "spring boot", "spring", "react", "angular", "vue", "django", "flask", "express", "node.js", "dotnet", "laravel", "rails"
    );
    
    private static final List<String> DATABASES = Arrays.asList(
        "mysql", "postgresql", "mongodb", "oracle", "sql server", "redis", "elasticsearch", "cassandra", "dynamodb"
    );
    
    private static final List<String> CLOUD_PLATFORMS = Arrays.asList(
        "aws", "azure", "google cloud", "gcp", "heroku", "digitalocean", "vercel", "netlify"
    );
    
    private static final List<String> DEVOPS_TOOLS = Arrays.asList(
        "docker", "kubernetes", "jenkins", "gitlab", "github actions", "terraform", "ansible", "puppet", "chef"
    );
    
    private static final List<String> TOOLS = Arrays.asList(
        "git", "jira", "confluence", "slack", "maven", "gradle", "npm", "yarn", "webpack", "babel", "postman"
    );

    private static final Pattern EXPERIENCE_PATTERN = Pattern.compile(
        "(\\d+)\\+?\\s*(?:years?|yrs?)\\s*(?:of\\s*)?(?:experience|exp)", 
        Pattern.CASE_INSENSITIVE
    );

    public JobDescription analyzeJobDescription(String jobDescriptionText) {
        JobDescription jobDesc = new JobDescription();
        String text = jobDescriptionText.toLowerCase();
        
        // Extract job title
        jobDesc.setJobTitle(extractJobTitle(jobDescriptionText));
        
        // Extract company name
        jobDesc.setCompany(extractCompany(jobDescriptionText));
        
        // Extract location
        jobDesc.setLocation(extractLocation(jobDescriptionText));
        
        // Extract experience level and years
        extractExperienceInfo(text, jobDesc);
        
        // Extract skills by category
        List<String> allSkills = new ArrayList<>();
        allSkills.addAll(extractSkillsByCategory(text, PROGRAMMING_LANGUAGES));
        allSkills.addAll(extractSkillsByCategory(text, FRAMEWORKS));
        allSkills.addAll(extractSkillsByCategory(text, DATABASES));
        allSkills.addAll(extractSkillsByCategory(text, CLOUD_PLATFORMS));
        allSkills.addAll(extractSkillsByCategory(text, DEVOPS_TOOLS));
        allSkills.addAll(extractSkillsByCategory(text, TOOLS));
        
        // Categorize skills into required and preferred
        categorizeSkills(jobDescriptionText, allSkills, jobDesc);
        
        // Extract responsibilities
        jobDesc.setResponsibilities(extractResponsibilities(jobDescriptionText));
        
        // Extract qualifications
        jobDesc.setQualifications(extractQualifications(jobDescriptionText));
        
        // Extract tools
        jobDesc.setTools(extractTools(jobDescriptionText));
        
        // Extract salary information
        jobDesc.setSalary(extractSalary(jobDescriptionText));
        
        return jobDesc;
    }

    private String extractJobTitle(String text) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && line.length() < 100) {
                // Look for common job title patterns
                if (line.toLowerCase().contains("engineer") || 
                    line.toLowerCase().contains("developer") ||
                    line.toLowerCase().contains("architect") ||
                    line.toLowerCase().contains("manager") ||
                    line.toLowerCase().contains("analyst") ||
                    line.toLowerCase().contains("consultant") ||
                    line.toLowerCase().contains("specialist") ||
                    line.toLowerCase().contains("lead") ||
                    line.toLowerCase().contains("senior") ||
                    line.toLowerCase().contains("junior")) {
                    return line;
                }
            }
        }
        return null;
    }

    private String extractCompany(String text) {
        // Look for company patterns - this is a simplified approach
        String[] lines = text.split("\n");
        for (int i = 0; i < Math.min(5, lines.length); i++) {
            String line = lines[i].trim();
            if (!line.toLowerCase().contains("job") && 
                !line.toLowerCase().contains("title") &&
                !line.toLowerCase().contains("location") &&
                line.length() < 50 && line.length() > 3) {
                return line;
            }
        }
        return null;
    }

    private String extractLocation(String text) {
        Pattern locationPattern = Pattern.compile(
            "\\b([A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*,\\s*[A-Z]{2}|[A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*)\\b",
            Pattern.MULTILINE
        );
        
        Matcher matcher = locationPattern.matcher(text);
        while (matcher.find()) {
            String location = matcher.group();
            if (!location.toLowerCase().contains("job") && 
                !location.toLowerCase().contains("description")) {
                return location;
            }
        }
        return null;
    }

    private void extractExperienceInfo(String text, JobDescription jobDesc) {
        Matcher matcher = EXPERIENCE_PATTERN.matcher(text);
        if (matcher.find()) {
            String years = matcher.group(1);
            jobDesc.setExperienceLevel(years + "+ years");
        } else {
            // Determine experience level from keywords
            if (text.contains("entry level") || text.contains("junior") || text.contains("associate")) {
                jobDesc.setExperienceLevel("Entry Level (0-2 years)");
            } else if (text.contains("mid level") || text.contains("intermediate")) {
                jobDesc.setExperienceLevel("Mid Level (3-5 years)");
            } else if (text.contains("senior") || text.contains("lead") || text.contains("principal")) {
                jobDesc.setExperienceLevel("Senior Level (5+ years)");
            } else if (text.contains("manager") || text.contains("director")) {
                jobDesc.setExperienceLevel("Management Level");
            }
        }
    }

    private List<String> extractSkillsByCategory(String text, List<String> categorySkills) {
        List<String> foundSkills = new ArrayList<>();
        for (String skill : categorySkills) {
            if (text.contains(skill)) {
                foundSkills.add(skill);
            }
        }
        return foundSkills;
    }

    private void categorizeSkills(String fullText, List<String> allSkills, JobDescription jobDesc) {
        List<String> required = new ArrayList<>();
        List<String> preferred = new ArrayList<>();
        
        String text = fullText.toLowerCase();
        
        for (String skill : allSkills) {
            // Check if skill is mentioned in required sections
            if (text.contains("required") && text.indexOf(skill) > text.indexOf("required") && 
                text.indexOf(skill) < text.indexOf("required") + 500) {
                required.add(skill);
            } else if (text.contains("must have") && text.indexOf(skill) > text.indexOf("must have") && 
                       text.indexOf(skill) < text.indexOf("must have") + 500) {
                required.add(skill);
            } else if (text.contains("essential") && text.indexOf(skill) > text.indexOf("essential") && 
                       text.indexOf(skill) < text.indexOf("essential") + 500) {
                required.add(skill);
            } else if (text.contains("preferred") || text.contains("nice to have") || 
                       text.contains("bonus") || text.contains("plus")) {
                preferred.add(skill);
            } else {
                // Default to required if not clearly specified
                required.add(skill);
            }
        }
        
        jobDesc.setRequiredSkills(required);
        jobDesc.setPreferredSkills(preferred);
    }

    private List<String> extractResponsibilities(String text) {
        List<String> responsibilities = new ArrayList<>();
        String[] lines = text.split("\n");
        boolean inResponsibilitiesSection = false;
        
        for (String line : lines) {
            String lowerLine = line.toLowerCase().trim();
            
            if (lowerLine.contains("responsibilit") || lowerLine.contains("what you'll do") || 
                lowerLine.contains("role") || lowerLine.contains("duties")) {
                inResponsibilitiesSection = true;
                continue;
            }
            
            if (inResponsibilitiesSection && (lowerLine.contains("requirement") || 
                lowerLine.contains("qualification") || lowerLine.contains("skill"))) {
                break;
            }
            
            if (inResponsibilitiesSection && !line.isEmpty()) {
                if (line.startsWith("•") || line.startsWith("-") || line.startsWith("*")) {
                    responsibilities.add(line.substring(1).trim());
                } else if (line.matches("\\d+\\..*")) {
                    responsibilities.add(line.substring(line.indexOf('.') + 1).trim());
                } else if (line.length() < 200) {
                    responsibilities.add(line.trim());
                }
            }
        }
        
        return responsibilities;
    }

    private List<String> extractQualifications(String text) {
        List<String> qualifications = new ArrayList<>();
        String[] lines = text.split("\n");
        boolean inQualificationsSection = false;
        
        for (String line : lines) {
            String lowerLine = line.toLowerCase().trim();
            
            if (lowerLine.contains("qualification") || lowerLine.contains("requirement") || 
                lowerLine.contains("education") || lowerLine.contains("degree")) {
                inQualificationsSection = true;
                continue;
            }
            
            if (inQualificationsSection && (lowerLine.contains("responsibilit") || 
                lowerLine.contains("benefit") || lowerLine.contains("about"))) {
                break;
            }
            
            if (inQualificationsSection && !line.isEmpty()) {
                if (line.startsWith("•") || line.startsWith("-") || line.startsWith("*")) {
                    qualifications.add(line.substring(1).trim());
                } else if (line.matches("\\d+\\..*")) {
                    qualifications.add(line.substring(line.indexOf('.') + 1).trim());
                } else if (line.length() < 200) {
                    qualifications.add(line.trim());
                }
            }
        }
        
        return qualifications;
    }

    private List<String> extractTools(String text) {
        List<String> tools = new ArrayList<>();
        
        // Extract tools that might not be in the predefined lists
        String[] words = text.toLowerCase().split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            String word = words[i];
            String nextWord = words[i + 1];
            
            // Check for common tool patterns
            if (word.equals("using") || word.equals("with") || word.equals("tools") || 
                word.equals("software") || word.equals("platform")) {
                String potentialTool = nextWord.replaceAll("[^a-zA-Z0-9]", "");
                if (potentialTool.length() > 2 && !tools.contains(potentialTool)) {
                    tools.add(potentialTool);
                }
            }
        }
        
        return tools;
    }

    private String extractSalary(String text) {
        Pattern salaryPattern = Pattern.compile(
            "\\$\\d{1,3}(?:,\\d{3})*(?:\\s*[-–]\\s*\\$\\d{1,3}(?:,\\d{3})*)?\\s*(?:per\\s*(?:year|annum|hour)|/year|/hour|annually|hourly)?",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = salaryPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        
        return null;
    }
}
