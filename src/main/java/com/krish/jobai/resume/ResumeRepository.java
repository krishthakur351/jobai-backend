package com.krish.jobai.resume;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository
        extends JpaRepository<Resume, Long> {

    Resume findByEmail(String email);
}