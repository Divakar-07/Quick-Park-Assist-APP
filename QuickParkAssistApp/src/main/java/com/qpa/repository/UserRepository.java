package com.qpa.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.qpa.entity.UserInfo;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
    
}