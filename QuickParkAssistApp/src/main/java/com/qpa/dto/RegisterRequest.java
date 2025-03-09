package com.qpa.dto;

import com.qpa.entity.AuthUser;
import com.qpa.entity.UserInfo;

public class RegisterRequest {
    private UserInfo user;
    private AuthUser authUser;

    // Getters and Setters
    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public AuthUser getAuthUser() {
        return authUser;
    }

    public void setAuthUser(AuthUser authUser) {
        this.authUser = authUser;
    }
}
