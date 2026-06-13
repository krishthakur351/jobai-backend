package com.krish.jobai.entity;
import lombok.Data;

import jakarta.persistence.*;
@Data

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    private String jobTitle;

    private String company;

    private String status;

    private String interviewDate;

    private String interviewLink;

    private String recruiterNote;

    public Application() {
    }

    public Application(
            String userEmail,
            String jobTitle,
            String company,
            String status
    ) {
        this.userEmail = userEmail;
        this.jobTitle = jobTitle;
        this.company = company;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(
            String interviewDate
    ) {
        this.interviewDate = interviewDate;
    }

    public String getInterviewLink() {
        return interviewLink;
    }

    public void setInterviewLink(
            String interviewLink
    ) {
        this.interviewLink = interviewLink;
    }

    public String getRecruiterNote() {
        return recruiterNote;
    }

    public void setRecruiterNote(
            String recruiterNote
    ) {
        this.recruiterNote = recruiterNote;
    }
}