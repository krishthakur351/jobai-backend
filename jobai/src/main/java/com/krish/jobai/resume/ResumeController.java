package com.krish.jobai.resume;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.HttpServerErrorException;


import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/resume")
@CrossOrigin("*")
public class ResumeController {

    public static String latestResume = "";

    @Value("${gemini.api.key}")
    private String API_KEY;

    @Autowired
    private ResumeRepository resumeRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("email") String email
    ) {

        try {

            PDDocument document =
                    Loader.loadPDF(file.getBytes());

            PDFTextStripper stripper =
                    new PDFTextStripper();

            String resumeText =
                    stripper.getText(document);

            List<String> detectedSkills =
                    new ArrayList<>();

            String[] skillKeywords = {

                    "Java",
                    "Spring Boot",
                    "React",
                    "JavaScript",
                    "HTML",
                    "CSS",
                    "MySQL",
                    "MongoDB",
                    "Git",
                    "GitHub",
                    "Docker",
                    "AWS",
                    "Python",
                    "Machine Learning",
                    "Deep Learning",
                    "C",
                    "C++"
            };

            for(String skill : skillKeywords){

                if(resumeText.toLowerCase()
                        .contains(skill.toLowerCase())){

                    detectedSkills.add(skill);
                }
            }

            latestResume = resumeText;

            document.close();

            String prompt =
                    "Analyze this resume and provide:\n" +
                            "1. Resume Score out of 100\n" +
                            "2. Strengths\n" +
                            "3. Missing Skills\n" +
                            "4. Improvement Suggestions\n\n" +
                            resumeText;

            String url =
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                            + API_KEY;

            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", prompt);

            JsonArray parts = new JsonArray();
            parts.add(textPart);

            JsonObject content = new JsonObject();
            content.add("parts", parts);

            JsonArray contents = new JsonArray();
            contents.add(content);

            JsonObject body = new JsonObject();
            body.add("contents", contents);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity =
                    new HttpEntity<>(
                            body.toString(),
                            headers
                    );

            RestTemplate restTemplate =
                    new RestTemplate();
            System.out.println(body.toString());
            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            String.class
                    );
            System.out.println(response.getBody());

            JsonObject jsonResponse =
                    JsonParser.parseString(
                            response.getBody()
                    ).getAsJsonObject();

            String aiResponse =
                    jsonResponse
                            .getAsJsonArray("candidates")
                            .get(0)
                            .getAsJsonObject()
                            .getAsJsonObject("content")
                            .getAsJsonArray("parts")
                            .get(0)
                            .getAsJsonObject()
                            .get("text")
                            .getAsString();

            Resume resume =
                    resumeRepository.findByEmail(email);

            if(resume == null){

                resume = new Resume();
            }

            resume.setEmail(email);

            resume.setFileName(
                    file.getOriginalFilename()
            );

            resume.setResumeText(
                    resumeText
            );

            resume.setSkills(
                    String.join(",",
                            detectedSkills)
            );
            resume.setAnalysis(aiResponse);

            if (resume.getStatus() == null) {
                resume.setStatus("Pending");
            }

            resume.setRecruiterNote("");

            resumeRepository.save(resume);

            Map<String,Object> result =
                    new HashMap<>();

            result.put(
                    "analysis",
                    aiResponse
            );

            result.put(
                    "skills",
                    detectedSkills
            );

            return ResponseEntity.ok(result);
        } catch (HttpServerErrorException.ServiceUnavailable e) {

            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Gemini AI is currently busy. Please try again in a few minutes.");
        }
        catch (HttpClientErrorException.TooManyRequests e) {

            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Gemini API quota exceeded. Please try again later.");
        }
        catch (HttpClientErrorException e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        }
        catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body("Resume Analysis Failed: " + e.getMessage());
        }
    }

    @GetMapping("/skills")
    public ResponseEntity<?> getSkills() {

        List<String> skills =
                new ArrayList<>();

        String text =
                latestResume.toLowerCase();

        if (text.contains("java")) {
            skills.add("Java");
        }

        if (text.contains("spring")) {
            skills.add("Spring Boot");
        }

        if (text.contains("react")) {
            skills.add("React");
        }

        if (text.contains("mysql")) {
            skills.add("MySQL");
        }

        if (text.contains("javascript")) {
            skills.add("JavaScript");
        }

        if (text.contains("python")) {
            skills.add("Python");
        }

        if (text.contains("git")) {
            skills.add("Git");
        }

        return ResponseEntity.ok(skills);
    }
    // ==============================
    // GET ALL RESUMES (ADMIN)
    // ==============================

    @GetMapping("/all")
    public ResponseEntity<?> getAllResumes() {

        return ResponseEntity.ok(
                resumeRepository.findAll()
        );
    }

    // ==============================
    // GET RESUME BY EMAIL
    // ==============================

    @GetMapping("/{email}")
    public ResponseEntity<?> getResumeByEmail(
            @PathVariable String email
    ) {

        Resume resume =
                resumeRepository.findByEmail(email);

        if (resume == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(resume);
    }

    // ==============================
    // GET USER SKILLS FROM DATABASE
    // ==============================

    @GetMapping("/skills/{email}")
    public ResponseEntity<?> getUserSkills(
            @PathVariable String email
    ) {

        Resume resume =
                resumeRepository.findByEmail(email);

        if (resume == null ||
                resume.getSkills() == null ||
                resume.getSkills().isEmpty()) {

            return ResponseEntity.ok(
                    new ArrayList<>()
            );
        }

        List<String> skills =
                Arrays.asList(
                        resume.getSkills().split(",")
                );

        return ResponseEntity.ok(skills);
    }

    @PutMapping("/status/{email}")
    public ResponseEntity<?> updateStatus(
            @PathVariable String email,
            @RequestParam String status,
            @RequestParam(required = false) String note
    ) {

        Resume resume =
                resumeRepository.findByEmail(email);

        if(resume == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Resume Not Found");
        }

        resume.setStatus(status);

        if(note != null) {
            resume.setRecruiterNote(note);
        }

        resumeRepository.save(resume);

        return ResponseEntity.ok("Updated");
    }

    // ==============================
    // AI JOB RECOMMENDATION
    // ==============================

    @GetMapping("/recommend/{email}")
    public ResponseEntity<?> recommendJobs(
            @PathVariable String email
    ) {

        Resume resume = resumeRepository.findByEmail(email);

        if (resume == null) {
            return ResponseEntity.badRequest()
                    .body("Upload Resume First");
        }


        String skills =
                resume.getSkills();

        Map<String, Object> result =
                new HashMap<>();

        result.put(
                "email",
                resume.getEmail()
        );

        result.put(
                "skills",
                skills
        );

        result.put(
                "recommendation",
                "Recommended roles: Java Developer, Full Stack Developer, Backend Developer based on skills -> "
                        + skills
        );

        return ResponseEntity.ok(result);
    }
}