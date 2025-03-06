package com.qpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qpa.entity.UserInfo;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByUsername(String username);

}