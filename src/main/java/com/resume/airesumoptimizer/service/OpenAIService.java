package com.resume.airesumoptimizer.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> optimizeResume(String resume, String jd) {
        try {

            // 🔥 CLEAN JSON-ONLY PROMPT (NO LATEX)
//             String prompt = """
// You are an expert ATS resume optimizer.

// IMPORTANT:
// - Do NOT fabricate fake experience
// - Only improve existing content
// - Align resume with job description
// - Return ONLY valid JSON
// - Do NOT return empty object

// ---

// RETURN STRICT JSON:

// {
//   "ats_score": number,
//   "matched_keywords": [],
//   "missing_keywords": [],
//   "improvements": [],
//   "optimized_resume": "",

//   "personal_info": {
//     "name": "",
//     "email": "",
//     "phone": "",
//     "linkedin": "",
//     "github": "",
//     "leetcode": ""
//   },

//   "summary": "",
//    "skills": {
//     "frontend": [],
//     "backend": [],
//     "database": [],
//     "cloud": [],
//     "tools": []
//   },
//   "experience": [
//     {
//       "role": "",
//       "company": "",
//       "duration": "",
//       "description": ""
//     }
//   ],

//   "projects": [
//     {
//       "name": "",
//       "tech": "",
//       "description": ""
//     }
//   ]
// }

// ---

// Resume:
// %s

// Job Description:
// %s
// """.formatted(resume, jd);
String prompt = """
You are a world-class ATS resume optimizer, technical resume writer, and hiring expert.

Your task is to intelligently transform and optimize the resume according to the job description while keeping the resume realistic, professional, ATS-friendly, and highly competitive.

====================================================
CORE OBJECTIVE
====================================================

Transform the resume to strongly align with the target role EVEN IF:
- technology stack differs
- domain differs
- role naming differs

Examples:
- Java Backend → Python Backend
- Backend Engineer → Data Engineer
- Full Stack → AI/ML Engineer
- Software Engineer → Cybersecurity Engineer

IMPORTANT:
- NEVER invent fake companies
- NEVER invent fake dates
- NEVER invent fake education
- KEEP employment history realistic
- You MAY intelligently adapt responsibilities, tools, projects, keywords, and technical terminology

====================================================
STEP 1 — ANALYZE JOB DESCRIPTION
====================================================

Identify:
- target role
- seniority
- domain
- required technologies
- preferred technologies
- tools/platforms
- ATS keywords
- business expectations

====================================================
STEP 2 — RESUME TRANSFORMATION
====================================================

A) PROFESSIONAL SUMMARY
- Write a powerful ATS-optimized summary
- Tailor it specifically to target role
- Include:
  - experience level
  - core technologies
  - domain expertise
  - business impact
- Keep concise and highly professional

----------------------------------------------------

B) EXPERIENCE SECTION (VERY IMPORTANT)

RULES:
- KEEP:
  - company names
  - role names
  - durations
- DO NOT fabricate employment
- Rewrite responsibilities to align with target role
- Add measurable impact and metrics
- Add scalable engineering terminology
- Add ATS keywords naturally
- Convert generic work into high-impact technical achievements

Examples:
- "Worked on APIs"
→ "Designed and developed scalable RESTful APIs serving high concurrent traffic"

- "Improved performance"
→ "Reduced API response latency by 40%% using caching and query optimization"

- "Backend services"
→ "Built distributed microservices architecture with asynchronous event-driven communication"

IMPORTANT:
- Group all responsibilities under ONE role entry
- DO NOT repeat same role multiple times
- Description must contain strong bullet-point style sentences

----------------------------------------------------

C) SKILLS SECTION (VERY IMPORTANT)

Rewrite skills completely according to job description.

RULES:
- Prioritize JD technologies
- Remove weak irrelevant skills
- Add logically inferable technologies
- Categorize professionally

Skill Categories:
- languages
- frontend
- backend
- database
- cloud
- devops
- testing
- tools

IMPORTANT:
- Skills MUST align with experience/projects
- Do NOT add random unrelated technologies

----------------------------------------------------

D) PROJECTS SECTION (HIGH IMPACT)

Projects can be transformed more aggressively than experience.

RULES:
- Modify project descriptions to align with target role
- Add business value
- Add metrics
- Add scalable architecture terminology
- Add ATS keywords naturally
- Improve technical sophistication

Examples:
- Backend app
→ scalable distributed platform
- CRUD app
→ enterprise-grade service platform
- Authentication project
→ secure identity/access management system

IMPORTANT:
- Keep projects realistic
- Do NOT generate impossible claims

----------------------------------------------------

E) ACHIEVEMENTS / CERTIFICATIONS

- Add strong resume-friendly wording
- Keep concise
- ATS optimized

====================================================
STEP 3 — ATS OPTIMIZATION
====================================================

- Inject important JD keywords naturally
- Improve keyword density
- Improve readability
- Improve recruiter impact
- Optimize for ATS parsing
- Make resume sound premium and modern

====================================================
OUTPUT RULES
====================================================

CRITICAL:
- Return ONLY VALID JSON
- NO markdown
- NO explanation
- NO extra text
- NO code blocks
- NO comments

Descriptions:
- concise
- impactful
- bullet-style
- ATS optimized

====================================================
OUTPUT JSON FORMAT
====================================================

{
  "ats_score": number,
  "matched_keywords": [],
  "missing_keywords": [],
  "improvements": [],
  "optimized_resume": "",

  "personal_info": {
    "name": "",
    "email": "",
    "phone": "",
    "linkedin": "",
    "github": "",
    "leetcode": ""
  },

  "summary": "",

  "skills": {
    "languages": [],
    "frontend": [],
    "backend": [],
    "database": [],
    "cloud": [],
    "devops": [],
    "testing": [],
    "tools": []
  },

  "education": [
    {
      "college": "",
      "degree": "",
      "duration": "",
      "score": ""
    }
  ],

  "experience": [
    {
      "role": "",
      "company": "",
      "duration": "",
      "description": [list of bullet points]
    }
  ],

  "projects": [
    {
      "name": "",
      "tech": "",
      "description": ""
    }
  ],

  "achievements": [],
  "certifications": []
}

====================================================
RESUME
====================================================

%s

====================================================
JOB DESCRIPTION
====================================================

%s
""".formatted(resume, jd);

            Map<String, Object> body = Map.of(
                    "model", "gpt-5.4-nano",
                    "input", prompt,
                    "temperature", 0.3,
                    "max_output_tokens", 3000,
                    "text", Map.of(
                            "format", Map.of("type", "json_object")
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            "https://api.openai.com/v1/responses",
                            request,
                            String.class
                    );

            JsonNode root = objectMapper.readTree(response.getBody());

            JsonNode output = root.path("output");

            if (!output.isArray() || output.size() == 0) {
                throw new RuntimeException("Invalid OpenAI response");
            }

            JsonNode content = output.get(0).path("content");

            if (!content.isArray() || content.size() == 0) {
                throw new RuntimeException("Invalid OpenAI content");
            }

            String text = content.get(0).path("text").asText();

            System.out.println("🔥 AI RAW RESPONSE:\n" + text); // DEBUG

            text = cleanJson(text);

            Map<String, Object> result = safeParseJson(text);

            // ✅ FALLBACK FIX (IMPORTANT)
            result.putIfAbsent("ats_score", 0);
            result.putIfAbsent("matched_keywords", List.of());
            result.putIfAbsent("missing_keywords", List.of());
            result.putIfAbsent("improvements", List.of());
            result.putIfAbsent("optimized_resume", "");

            return result;

        } catch (Exception e) {
            throw new RuntimeException("OpenAI failed: " + e.getMessage(), e);
        }
    }

    private String cleanJson(String text) {
        return text
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }

    private Map<String, Object> safeParseJson(String text) {
        try {
            return objectMapper.readValue(text, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON from AI: " + text);
        }
    }
}