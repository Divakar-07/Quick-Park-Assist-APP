package com.qpa.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.qpa.entity.UserInfo;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);
}