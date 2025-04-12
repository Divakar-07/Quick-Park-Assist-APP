package com.qpa.dto;

import com.qpa.entity.UserType;
import java.util.Set;

public class AuthResponse {
    private Long userId;
    private String username;
    private Set<UserType> roles;
    private String message;

    public AuthResponse() {
		
	}

	public AuthResponse(Long userId, String username, Set<UserType> roles, String message) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
        this.message = message;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Set<UserType> getRoles() { return roles; }
    public void setRoles(Set<UserType> roles) { this.roles = roles; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

