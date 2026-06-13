package com.krish.jobai.service;

import com.krish.jobai.entity.Job;
import com.krish.jobai.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    // ADD JOB
    public Job addJob(Job job) {
        return jobRepository.save(job);
    }

    // GET ALL JOBS
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // GET JOB BY ID
    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    // UPDATE JOB
    public Job updateJob(Long id, Job updatedJob) {

        Optional<Job> optionalJob =
                jobRepository.findById(id);

        if(optionalJob.isPresent()) {

            Job existingJob = optionalJob.get();

            existingJob.setTitle(updatedJob.getTitle());
            existingJob.setCompany(updatedJob.getCompany());
            existingJob.setLocation(updatedJob.getLocation());
            existingJob.setSalary(updatedJob.getSalary());
            existingJob.setDescription(updatedJob.getDescription());
            existingJob.setSkills(updatedJob.getSkills());

            return jobRepository.save(existingJob);
        }

        return null;
    }

    // DELETE JOB
    public String deleteJob(Long id) {

        jobRepository.deleteById(id);

        return "Job deleted successfully";
    }
}