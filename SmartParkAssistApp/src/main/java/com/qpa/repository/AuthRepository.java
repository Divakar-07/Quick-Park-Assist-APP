package com.qpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qpa.entity.AuthUser;

@Repository
public interface AuthRepository extends JpaRepository<AuthUser, Long> {
    
    @Query("SELECT a FROM AuthUser a WHERE a.email = :email")
    Optional<AuthUser> findFreshByEmail(@Param("email") String email);

    Optional<AuthUser> findByUser_UserId(Long userId);

}
