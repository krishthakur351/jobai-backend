package com.krish.jobai.repository;

import com.krish.jobai.entity.Application;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    List<Application> findByUserEmail(
            String userEmail
    );
}