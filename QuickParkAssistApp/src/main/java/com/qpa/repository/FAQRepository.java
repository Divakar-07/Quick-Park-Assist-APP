package com.qpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qpa.entity.FAQ;

public interface FAQRepository extends JpaRepository<FAQ, Long> {
}