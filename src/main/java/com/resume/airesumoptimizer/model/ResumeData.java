package com.resume.airesumoptimizer.model;

import java.util.List;

public class ResumeData {
    private String name;
    private String email;
    private String phone;
    private String summary;
    private List<Experience> experience;
    private List<Education> education;
    private List<Project> projects;
    private List<String> skills;
    private List<String> certifications;
    private String location;

    // Constructors
    public ResumeData() {}

    public ResumeData(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<Experience> getExperience() { return experience; }
    public void setExperience(List<Experience> experience) { this.experience = experience; }

    public List<Education> getEducation() { return education; }
    public void setEducation(List<Education> education) { this.education = education; }

    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<String> getCertifications() { return certifications; }
    public void setCertifications(List<String> certifications) { this.certifications = certifications; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public static class Experience {
        private String company;
        private String position;
        private String duration;
        private List<String> responsibilities;
        private String location;

        // Constructors
        public Experience() {}

        public Experience(String company, String position, String duration) {
            this.company = company;
            this.position = position;
            this.duration = duration;
        }

        // Getters and Setters
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }

        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }

        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }

        public List<String> getResponsibilities() { return responsibilities; }
        public void setResponsibilities(List<String> responsibilities) { this.responsibilities = responsibilities; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }

    public static class Education {
        private String institution;
        private String degree;
        private String field;
        private String graduationYear;
        private String gpa;

        // Constructors
        public Education() {}

        public Education(String institution, String degree, String field) {
            this.institution = institution;
            this.degree = degree;
            this.field = field;
        }

        // Getters and Setters
        public String getInstitution() { return institution; }
        public void setInstitution(String institution) { this.institution = institution; }

        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }

        public String getGraduationYear() { return graduationYear; }
        public void setGraduationYear(String graduationYear) { this.graduationYear = graduationYear; }

        public String getGpa() { return gpa; }
        public void setGpa(String gpa) { this.gpa = gpa; }
    }

    public static class Project {
        private String name;
        private String description;
        private List<String> technologies;
        private List<String> achievements;
        private String duration;

        // Constructors
        public Project() {}

        public Project(String name, String description) {
            this.name = name;
            this.description = description;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public List<String> getTechnologies() { return technologies; }
        public void setTechnologies(List<String> technologies) { this.technologies = technologies; }

        public List<String> getAchievements() { return achievements; }
        public void setAchievements(List<String> achievements) { this.achievements = achievements; }

        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
    }
}
