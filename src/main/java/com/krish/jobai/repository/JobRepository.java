package com.krish.jobai.repository;

import com.krish.jobai.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository
        extends JpaRepository<Job, Long> {

}