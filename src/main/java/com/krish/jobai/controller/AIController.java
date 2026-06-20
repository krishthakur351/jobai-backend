package com.krish.jobai.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.krish.jobai.resume.Resume;
import com.krish.jobai.resume.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import com.krish.jobai.dto.ChatRequest;
@RestController
@RequestMapping("/api/ai")
@CrossOrigin("*")
public class AIController {
    @Autowired
    private ResumeRepository resumeRepository;
    @Value("${gemini.api.key}")
    private String API_KEY;
    @PostMapping("/chat")
    public ResponseEntity<?> chatWithAI(
            @RequestBody ChatRequest request
    ) {

        try {

            String email = request.getEmail();
            String question = request.getQuestion();

            String url =
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                            + API_KEY;

            Resume resume = resumeRepository.findByEmail(email);

            if (resume == null) {
                return ResponseEntity.badRequest()
                        .body("Please upload resume first.");
            }

            String prompt =
                    "You are an AI Career Assistant.\n\n" +

                            "User Resume:\n" +
                            resume.getResumeText() +

                            "\n\nUser Question:\n" +
                            question +

                            "\n\nAnswer based on the user's resume whenever possible.";

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

            RestTemplate restTemplate = new RestTemplate();

            System.out.println("Question = " + question);
            System.out.println("API Key = " + API_KEY);
            System.out.println("URL = " + url);

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            String.class
                    );

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

            return ResponseEntity.ok(aiResponse);

        }
        catch (HttpServerErrorException.ServiceUnavailable e) {

            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(
                            "Gemini AI is currently busy. Please try again in a few minutes."
                    );
        }
        catch (HttpClientErrorException.TooManyRequests e) {

            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Gemini API quota exceeded. Please try again later.");
        }
        catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "AI Error: " + e.getMessage()
                    );

        }
    }
}