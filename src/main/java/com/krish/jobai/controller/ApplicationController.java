package com.krish.jobai.controller;

import com.krish.jobai.entity.Application;
import com.krish.jobai.repository.ApplicationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin("*")
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;

    // APPLY JOB
    @PostMapping
    public Application applyJob(
            @RequestBody Application application
    ) {

        application.setStatus("Applied ✅");

        return applicationRepository.save(application);
    }

    // GET USER APPLICATIONS
    @GetMapping("/{email}")
    public List<Application> getApplications(
            @PathVariable String email
    ) {

        return applicationRepository.findByUserEmail(
                email
        );
    }

    // GET ALL APPLICATIONS (ADMIN)
    @GetMapping
    public List<Application> getAllApplications() {

        return applicationRepository.findAll();
    }

    // UPDATE STATUS
    @PutMapping("/{id}")
    public Application updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {

        Application application =
                applicationRepository.findById(id)
                        .orElseThrow();

        application.setStatus(status);

        return applicationRepository.save(
                application
        );
    }
}