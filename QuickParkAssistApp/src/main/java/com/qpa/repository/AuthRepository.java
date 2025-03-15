package com.qpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qpa.entity.AuthUser;

@Repository
public interface AuthRepository extends JpaRepository<AuthUser, Long> {
    
    @Query("SELECT a FROM AuthUser a WHERE a.username = :username")
    Optional<AuthUser> findFreshByUsername(@Param("username") String username);

    Optional<AuthUser> findByUser_UserId(Long userId);

}
