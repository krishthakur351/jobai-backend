package com.krish.jobai.controller;

import com.krish.jobai.entity.Job;
import com.krish.jobai.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    // ADD JOB
    @PostMapping
    public Job addJob(@RequestBody Job job) {
        return jobService.addJob(job);
    }

    // GET ALL JOBS
    @GetMapping
    public List<Job> getAllJobs() {
        return jobService.getAllJobs();
    }

    // GET JOB BY ID
    @GetMapping("/{id}")
    public Optional<Job> getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }

    // UPDATE JOB
    @PutMapping("/{id}")
    public Job updateJob(
            @PathVariable Long id,
            @RequestBody Job updatedJob
    ) {
        return jobService.updateJob(id, updatedJob);
    }

    // DELETE JOB
    @DeleteMapping("/{id}")
    public String deleteJob(@PathVariable Long id) {
        return jobService.deleteJob(id);
    }
}