package com.resume.airesumoptimizer.service;

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

//             // 🔥 STRONG PROMPT (UPGRADED)
//            String prompt = """
// You are a world-class ATS resume optimizer, career strategist, and hiring expert.

// INPUT:
// Resume:
// %s

// Job Description:
// %s

// OBJECTIVE:
// Completely transform the resume to match the job description, EVEN IF the domain, role, or technology stack is different.

// ---

// STEP 1: IDENTIFY TARGET ROLE
// - Analyze the job description
// - Identify:
//   - Target role (e.g., .NET Developer, Data Scientist, Python Developer)
//   - Required skills
//   - Tools, frameworks, domain

// ---

// STEP 2: TRANSFORM RESUME (CRITICAL)

// A. EXPERIENCE TRANSFORMATION:
// - KEEP same company names, job titles, and dates (NO fake companies)
// - BUT rewrite responsibilities to match target role
// - Adapt work to align with job description
// - Example:
//   - Java → Python → rewrite as Python-based work
//   - Backend → Data Science → rewrite tasks as data handling, ML, analytics
// - Maintain realistic career progression

// B. SKILLS TRANSFORMATION (VERY IMPORTANT):
// - Completely rewrite skills section based on job description
// - ADD required skills from JD
// - REMOVE irrelevant skills
// - Ensure skills match rewritten experience
// - Dynamically adapt to ANY domain:
//   - Programming languages
//   - Frameworks
//   - Tools
//   - Cloud technologies

// C. PROJECT/WORK ALIGNMENT:
// - Modify project descriptions to reflect:
//   - Target technologies
//   - Business impact
//   - Job responsibilities
// - Add measurable metrics:
//   - "Improved performance by 30%%"
//   - "Reduced processing time by 40%%"
//   - "Handled 50K+ records/users"

// ---

// STEP 3: ATS OPTIMIZATION
// - Inject keywords from job description naturally
// - Ensure high keyword match score
// - Make resume pass ATS filters

// ---

// STEP 4: REALISM CONSTRAINT
// - DO NOT create fake companies
// - DO NOT invent completely unrealistic experience
// - BUT reinterpret and adapt existing experience intelligently

// ---

// OUTPUT FORMAT (STRICT JSON ONLY):

// {
//   "ats_score": number,
//   "matched_keywords": [],
//   "missing_keywords": [],
//   "improvements": [
//     "Explain transformation (e.g., converted backend to data science, updated skills, added metrics)"
//   ],
//   "optimized_resume": "FULLY TRANSFORMED, JOB-SPECIFIC RESUME"
// }

// ---

// IMPORTANT:
// - ONLY return valid JSON
// - NO markdown
// - NO ```json
// - Resume MUST match job description domain
// - Skills MUST reflect job requirements
// - Experience MUST align with target role
// """.formatted(resume, jd);

String prompt = """
You are a world-class ATS resume optimizer, career strategist, and LaTeX resume generator.

INPUT:
Resume:
%s

Job Description:
%s

OBJECTIVE:
Completely transform the resume to match the job description, EVEN IF the domain or technology stack is different.

---

STEP 1: IDENTIFY TARGET ROLE
- Analyze job description
- Identify role, skills, tools, domain

---

STEP 2: TRANSFORM RESUME

A. EXPERIENCE:
- KEEP same company names, roles, dates
- Rewrite responsibilities to match job description
- Adapt domain dynamically (Java → Python, Backend → Data Science, etc.)
- Add measurable impact:
  - "Improved performance by 30%%"
  - "Reduced latency by 40%%"

B. SKILLS (VERY IMPORTANT):
- Rewrite skills completely based on job description
- Add required skills
- Remove irrelevant skills
- Ensure alignment with experience

C. PROJECTS:
- Modify to reflect target role
- Add business impact + metrics

---

STEP 3: ATS OPTIMIZATION
- Inject job description keywords
- Improve ATS score

---

LATEX GENERATION RULES (STRICT):

- Use EXACT template below
- DO NOT change structure
- ONLY replace content
- Keep formatting clean and professional
- Escape special characters properly

---

LATEX TEMPLATE:

\\documentclass[letterpaper,11pt]{article}

\\usepackage{latexsym}
\\usepackage[empty]{fullpage}
\\usepackage{titlesec}
\\usepackage{marvosym}
\\usepackage[usenames,dvipsnames]{color}
\\usepackage{verbatim}
\\usepackage{enumitem}
\\usepackage[hidelinks]{hyperref}
\\usepackage{fancyhdr}
\\usepackage[english]{babel}
\\usepackage{tabularx}
\\input{glyphtounicode}

\\pagestyle{fancy}
\\fancyhf{}
\\fancyfoot{}
\\renewcommand{\\headrulewidth}{0pt}
\\renewcommand{\\footrulewidth}{0pt}

\\addtolength{\\oddsidemargin}{-0.5in}
\\addtolength{\\evensidemargin}{-0.5in}
\\addtolength{\\textwidth}{1in}
\\addtolength{\\topmargin}{-.5in}
\\addtolength{\\textheight}{1.0in}

\\urlstyle{same}
\\raggedbottom
\\raggedright
\\setlength{\\tabcolsep}{0in}

\\titleformat{\\section}{
  \\vspace{-4pt}\\scshape\\raggedright\\large
}{}{0em}{imp}[\\color{black}\\titlerule \\vspace{-5pt}]

\\pdfgentounicode=1

\\newcommand{\\resumeItem}[1]{
  \\item\\small{{#1 \\vspace{-2pt}}}
}

\\newcommand{\\resumeSubheading}[4]{
  \\vspace{-2pt}\\item
    \\begin{tabular*}{0.97\\textwidth}[t]{l@{\\extracolsep{\\fill}}r}
      \\textbf{#1} & #2 \\\\
      \\textit{\\small#3} & \\textit{\\small #4} \\\\
    \\end{tabular*}\\vspace{-7pt}
}

\\newcommand{\\resumeSubHeadingListStart}{\\begin{itemize}[leftmargin=0.15in, label={}]}
\\newcommand{\\resumeSubHeadingListEnd}{\\end{itemize}}
\\newcommand{\\resumeItemListStart}{\\begin{itemize}}
\\newcommand{\\resumeItemListEnd}{\\end{itemize}\\vspace{-5pt}}

\\begin{document}

\\begin{center}
    \\textbf{\\Huge \\scshape <NAME>} \\\\
    \\small <PHONE> $|$ <EMAIL> $|$ <LINKEDIN> $|$ <GITHUB>
\\end{center}

\\section{Summary}
<summary>

\\section{Experience}
\\resumeSubHeadingListStart
<experience entries using resumeSubheading + resumeItem>
\\resumeSubHeadingListEnd

\\section{Education}
\\resumeSubHeadingListStart
<education>
\\resumeSubHeadingListEnd

\\section{Projects}
\\resumeSubHeadingListStart
<projects>
\\resumeSubHeadingListEnd

\\section{Technical Skills}
<skills>

\\end{document}

---

OUTPUT FORMAT (STRICT JSON ONLY):

{
  "ats_score": number,
  "matched_keywords": [],
  "missing_keywords": [],
  "improvements": [],
  "optimized_resume": "",
  "optimized_resume_latex": ""
}

---

IMPORTANT:
- ONLY return valid JSON
- NO markdown
- NO ```json
- Resume MUST match job description
- Skills MUST reflect JD
- Experience MUST align with role
- don't miss this line }{}{0em}{}[\\color{black}\\titlerule \\vspace{-5pt}] in title section
- don't add the // and \\ slashes .te file
LATEX STRICT RULES:
  1.In LaTeX commands, NEVER remove empty braces {} IMPPORTANT.
  2. They are REQUIRED even if empty.
 -Make sure titleformat i need horizontal line after section title. This is critical for formatting. The line must be exactly like this:
-}{}{0em}{}[\\color{black}\\titlerule \\vspace{-5pt}]
-DO NOT use \\& — ALWAYS use single forward slash & in LaTeX tables
""".formatted(resume, jd);

            // // 🔥 BODY WITH RESPONSE FORMAT (IMPORTANT)
            // Map<String, Object> body = Map.of(
            //         "model", "gpt-5.4-nano",
            //         "input", prompt,
            //         "temperature", 0.3,
            //         "max_output_tokens", 2000
            // );

          Map<String, Object> body = Map.of(
        "model", "gpt-5.4-nano",
        "input", prompt,
        "temperature", 0.3,
        "max_output_tokens", 4000,
        "text", Map.of(                      // 🔥 NEW FIX
            "format", Map.of(
                "type", "json_object"
            )
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

            String text = root
                    .path("output")
                    .get(0)
                    .path("content")
                    .get(0)
                    .path("text")
                    .asText();

            // 🔥 CLEAN RESPONSE
            text = cleanJson(text);

            // 🔥 SAFE PARSE (IMPORTANT)
            return safeParseJson(text);

        } catch (Exception e) {
            throw new RuntimeException("OpenAI failed: " + e.getMessage(), e);
        }
    }

    /**
     * 🔥 Clean markdown if model still adds it
     */
    private String cleanJson(String text) {
        return text
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }

    /**
     * 🔥 SAFE JSON PARSE (VERY IMPORTANT)
     */
    private Map<String, Object> safeParseJson(String text) {
        try {
            return objectMapper.readValue(text, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON from AI: " + text);
        }
    }
}