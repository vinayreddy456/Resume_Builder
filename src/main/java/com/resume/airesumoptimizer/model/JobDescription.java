package com.resume.airesumoptimizer.model;

import java.util.List;

public class JobDescription {
    private String jobTitle;
    private String company;
    private String location;
    private String description;
    private List<String> requiredSkills;
    private List<String> preferredSkills;
    private List<String> responsibilities;
    private List<String> qualifications;
    private List<String> tools;
    private String experienceLevel;
    private String salary;

    // Constructors
    public JobDescription() {}

    public JobDescription(String jobTitle, String description) {
        this.jobTitle = jobTitle;
        this.description = description;
    }

    // Getters and Setters
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public List<String> getPreferredSkills() { return preferredSkills; }
    public void setPreferredSkills(List<String> preferredSkills) { this.preferredSkills = preferredSkills; }

    public List<String> getResponsibilities() { return responsibilities; }
    public void setResponsibilities(List<String> responsibilities) { this.responsibilities = responsibilities; }

    public List<String> getQualifications() { return qualifications; }
    public void setQualifications(List<String> qualifications) { this.qualifications = qualifications; }

    public List<String> getTools() { return tools; }
    public void setTools(List<String> tools) { this.tools = tools; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }
}
