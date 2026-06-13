package com.krish.jobai.dto;

public class ChatRequest {

    private String email;
    private String question;

    public ChatRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}