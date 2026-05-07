package com.resume.airesumoptimizer.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ResumeHtmlService {

    public String generateHtml(Map<String, Object> data) {

        Map<String, Object> personal = (Map<String, Object>) data.get("personal_info");

        String name = escape(get(personal, "name"));
        String email = escape(get(personal, "email"));
        String phone = escape(get(personal, "phone"));
        String linkedin = escape(get(personal, "linkedin"));
        String github = escape(get(personal, "github"));
        String leetcode = escape(get(personal, "leetcode"));

        String summary = escape(get(data, "summary"));

        Map<String, List<String>> skills =
                (Map<String, List<String>>) data.get("skills");
        if (skills == null) skills = Map.of();

        // List<Map<String, String>> experience =
        //         (List<Map<String, String>>) data.get("experience");
        // if (experience == null) experience = List.of();

        List<Map<String, Object>> experience =
        (List<Map<String, Object>>) data.get("experience");

if (experience == null) {
    experience = List.of();
}

        List<Map<String, String>> projects =
                (List<Map<String, String>>) data.get("projects");
        if (projects == null) projects = List.of();

        List<Map<String, String>> education =
                (List<Map<String, String>>) data.get("education");
        if (education == null) education = List.of();

        List<String> achievements =
                (List<String>) data.get("achievements");
        if (achievements == null) achievements = List.of();

        List<String> certifications =
                (List<String>) data.get("certifications");
        if (certifications == null) certifications = List.of();

        // 🔥 CONTACT LINE (no empty pipes)
        String contact = buildContact(phone, email, github, linkedin, leetcode);

        // 🔥 SKILLS
        String skillsHtml = buildSkills(skills);

        // 🔥 CONDITIONAL SECTIONS
        String educationHtml = education.isEmpty() ? "" :
                buildSection("EDUCATION", buildEducation(education));

        String experienceHtml = isValidExperience(experience) ?
        buildSection("EXPERIENCE", buildExperience(experience)) : "";

        String projectsHtml = projects.isEmpty() ? "" :
                buildSection("PROJECTS", buildProjects(projects));

        String achievementsHtml = achievements.isEmpty() ? "" :
                buildSection("ACHIEVEMENTS", buildSimpleList(achievements));

        String certificationsHtml = certifications.isEmpty() ? "" :
                buildSection("CERTIFICATIONS", buildSimpleList(certifications));

        return """
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta charset="UTF-8"/>

<style>

/* 🔥 LATEX EXACT PAGE */
@page {
  size: A4;
  margin: 2cm;
}

/* BODY */
body {
  font-family: "Charter";
  font-size: 10pt;
  line-height: 1.25;
  margin: 0;
  padding: 0;
}

/* HEADER */
.header {
  text-align: center;
  margin-bottom: 6px;
}

.name {
  font-size: 24px;
  font-weight: bold;
}

.contact {
  font-size: 12.5px;
  margin-top: 3px;
}

/* SECTION */
.section {
  margin-top: 6px;
}

.section-title {
  font-weight: bold;
  font-size: 13px;
  border-bottom: 1px solid black;
  margin-bottom: 3px;
  padding-bottom: 1px;
}

/* TEXT */
p {
  margin: 1px 0;
}

/* ROLE */
.role {
  font-weight: bold;
}

.subtext {
  font-style: italic;
  font-size: 11.5px;
  margin-bottom: 2px;
}

/* BULLETS (LATEX STYLE) */
ul {
  margin: 2px 0;
  padding-left: 10px;   /* 🔥 tighter like LaTeX */
}

li {
  margin-bottom: 0px;   /* 🔥 EXACT LaTeX behavior */
  line-height: 1.25;
}

/* REMOVE EXTRA SPACE */
div {
  margin: 0;
  padding: 0;
}

.edu {
  margin-bottom: 4px;
}

.edu-header,
.edu-sub {
  display: flex;
  justify-content: space-between;
}

.college {
  font-weight: bold;
}

.degree {
  font-size: 11px;
}

.duration,
.score {
  text-align: right;
  font-size: 10.5px;
}
  .edu-table {
  width: 100%%;
  border-collapse: collapse;
  margin-bottom: 4px;
}

.left {
  text-align: left;
}

.right {
  text-align: right;
  white-space: nowrap;
}


.exp-table {
  width: 100%%;
  border-collapse: collapse;
  margin-bottom: 5px;
}

.exp-left {
  text-align: left;
}

.exp-right {
  text-align: right;
  white-space: nowrap;
  vertical-align: top;
}

.role {
  font-weight: bold;
}
</style>
</head>

<body>

<div class="header">
  <div class="name">%s</div>
  <div class="contact">%s</div>
</div>

%s
%s
%s
%s
%s
%s

</body>
</html>
"""
.formatted(
        name,
        contact,
        buildSection("SUMMARY", "<p>" + summary + "</p>"),
        buildSection("SKILLS", skillsHtml),
        educationHtml,
        experienceHtml,
        projectsHtml,
        achievementsHtml + certificationsHtml
);
    }

    // 🔥 CONTACT BUILDER
    private String buildContact(String... fields) {
        return Arrays.stream(fields)
                .filter(f -> f != null && !f.isBlank())
                .collect(Collectors.joining(" | "));
    }

    // 🔥 SECTION WRAPPER
    private String buildSection(String title, String content) {
        if (content == null || content.isBlank()) return "";
        return """
<div class="section">
  <div class="section-title">%s</div>
  %s
</div>
""".formatted(title, content);
    }

    // 🔥 SKILLS
private String buildSkills(Map<String, List<String>> skills) {
    return skills.entrySet().stream()
            .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
            .map(e -> "<div style='margin-bottom:2px;'>" +
                    "<b>" + capitalize(e.getKey()) + ":</b> " +
                    e.getValue().stream()
                            .map(this::escape)
                            .collect(Collectors.joining(", ")) +
                    "</div>")
            .collect(Collectors.joining());
}
// private boolean isValidExperience(List<Map<String, Object>> experience) {
//     if (experience == null || experience.isEmpty()) return false;

//     return experience.stream().anyMatch(e ->
//             notEmpty(e.get("role")) ||
//             notEmpty(e.get("company")) ||
//             notEmpty(e.get("description"))
//     );
// }

private boolean isValidExperience(List<Map<String, Object>> experience) {

    if (experience == null || experience.isEmpty()) {
        return false;
    }

    return experience.stream().anyMatch(e -> {

        List<String> descriptions =
                (List<String>) e.get("description");

        return notEmpty((String) e.get("role")) ||
               notEmpty((String) e.get("company")) ||
               (descriptions != null && !descriptions.isEmpty());
    });
}
private boolean notEmpty(String s) {
    return s != null && !s.trim().isEmpty();
}
    // 🔥 EDUCATION
    // private String buildEducation(List<Map<String, String>> eduList) {
    //     StringBuilder sb = new StringBuilder();

    //     for (Map<String, String> e : eduList) {
    //         sb.append("<div>")
    //           .append("<div class='role'>")
    //           .append(escape(e.get("college")))
    //           .append("</div>")
    //           .append("<div class='subtext'>")
    //           .append(escape(e.get("degree")))
    //           .append(" | ")
    //           .append(escape(e.get("duration")))
    //           .append(" | ")
    //           .append(escape(e.get("score")))
    //           .append("</div>")
    //           .append("</div>");
    //     }
    //     return sb.toString();
    // }
private String buildEducation(List<Map<String, String>> eduList) {
    StringBuilder sb = new StringBuilder();

    for (Map<String, String> e : eduList) {

        String college = escape(e.get("college"));
        String degree = escape(e.get("degree"));
        String duration = escape(e.get("duration"));
        String score = escape(e.get("score"));

        // sb.append("<div class='edu'>")

        //   // 🔥 First Row: College (left) | Duration (right)
        //   .append("<div class='edu-header'>")
        //   .append("<span class='college'>").append(college).append("</span>")
        //   .append("<span class='duration'>").append(duration).append("</span>")
        //   .append("</div>")

        //   // 🔥 Second Row: Degree (left) | Score (right)
        //   .append("<div class='edu-sub'>")
        //   .append("<span class='degree'>").append(degree).append("</span>")
        //   .append("<span class='score'>").append(score).append("</span>")
        //   .append("</div>")

        //   .append("</div>");

        sb.append("<table class='edu-table'>")
  .append("<tr>")
  .append("<td class='left'><b>").append(college).append("</b></td>")
  .append("<td class='right'>").append(duration).append("</td>")
  .append("</tr>")

  .append("<tr>")
  .append("<td class='left'>").append(degree).append("</td>")
  .append("<td class='right'>").append(score).append("</td>")
  .append("</tr>")
  .append("</table>");
    }

    return sb.toString();
}
    // 🔥 EXPERIENCE
    // private String buildExperience(List<Map<String, String>> expList) {
    //     StringBuilder sb = new StringBuilder();

    //     for (Map<String, String> e : expList) {
    //         sb.append("<div>")
    //           .append("<div class='role'>")
    //           .append(escape(e.get("role")))
    //           .append("</div>")
    //           .append("<div class='subtext'>")
    //           .append(escape(e.get("company")))
    //           .append(" | ")
    //           .append(escape(e.get("duration")))
    //           .append("</div>")
    //           .append("<ul>")
    //           .append(buildBulletPoints(e.get("description")))
    //           .append("</ul>")
    //           .append("</div>");
    //     }
    //     return sb.toString();
    // }
//     private String buildExperience(List<Map<String, String>> expList) {

//     StringBuilder sb = new StringBuilder();

//     for (Map<String, String> e : expList) {

//         String role = escape(e.get("role"));
//         String company = escape(e.get("company"));
//         String duration = escape(e.get("duration"));
//         String description = e.get("description");

//         sb.append("<table class=\"exp-table\">")

//           // 🔥 HEADER ROW
//           .append("<tr>")
//           .append("<td class=\"exp-left\">")
//           .append("<span class=\"role\">")
//           .append(role);

//         // show company only if exists
//         if (company != null && !company.isBlank()) {
//             sb.append(" - ").append(company);
//         }

//         sb.append("</span>")
//           .append("</td>")

//           .append("<td class=\"exp-right\">")
//           .append(duration)
//           .append("</td>")
//           .append("</tr>")

//           // 🔥 BULLETS
//           .append("<tr>")
//           .append("<td colspan=\"2\">")
//           .append("<ul>")
//           .append(buildBulletPoints(description))
//           .append("</ul>")
//           .append("</td>")
//           .append("</tr>")

//           .append("</table>");
//     }

//     return sb.toString();
// }

private String buildExperience(List<Map<String, Object>> expList) {

    StringBuilder sb = new StringBuilder();

    for (Map<String, Object> e : expList) {

        String role = escape((String) e.get("role"));
        String company = escape((String) e.get("company"));
        String duration = escape((String) e.get("duration"));

        List<String> descriptions =
                (List<String>) e.get("description");

        sb.append("<table class=\"exp-table\">")

          // 🔥 HEADER ROW
          .append("<tr>")
          .append("<td class=\"exp-left\">")
          .append("<span class=\"role\">")
          .append(role);

        // company
        if (company != null && !company.isBlank()) {
            sb.append(" - ").append(company);
        }

        sb.append("</span>")
          .append("</td>")

          // duration
          .append("<td class=\"exp-right\">")
          .append(duration)
          .append("</td>")
          .append("</tr>")

          // 🔥 BULLET SECTION
          .append("<tr>")
          .append("<td colspan=\"2\">")
          .append("<ul>");

        // bullet points directly
        if (descriptions != null) {

            for (String point : descriptions) {

                if (point != null && !point.isBlank()) {

                    sb.append("<li>")
                      .append(escape(point))
                      .append("</li>");
                }
            }
        }

        sb.append("</ul>")
          .append("</td>")
          .append("</tr>")

          .append("</table>");
    }

    return sb.toString();
}

    // 🔥 PROJECTS
    private String buildProjects(List<Map<String, String>> projList) {
        StringBuilder sb = new StringBuilder();

        for (Map<String, String> p : projList) {
            sb.append("<div>")
              .append("<div class='role'>")
              .append(escape(p.get("name")))
              .append("</div>")
              .append("<div class='subtext'>")
              .append(escape(p.get("tech")))
              .append("</div>")
              .append("<ul>")
              .append(buildBulletPoints(p.get("description")))
              .append("</ul>")
              .append("</div>");
        }
        return sb.toString();
    }

    // 🔥 SIMPLE LIST
    private String buildSimpleList(List<String> items) {
        return "<ul>" + items.stream()
                .map(i -> "<li>" + escape(i) + "</li>")
                .collect(Collectors.joining()) + "</ul>";
    }

    // 🔥 BULLET SPLITTER
    private String buildBulletPoints(String text) {
        if (text == null) return "";
        String[] points = text.split("\\. ");
        StringBuilder sb = new StringBuilder();

        for (String p : points) {
            if (!p.trim().isEmpty()) {
                sb.append("<li>").append(escape(p.trim())).append("</li>");
            }
        }
        return sb.toString();
    }

    // 🔥 ESCAPE HTML
    private String escape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;");
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0,1).toUpperCase() + text.substring(1);
    }

    private String get(Map<String, Object> map, String key) {
        return map != null && map.get(key) != null ? map.get(key).toString() : "";
    }
}