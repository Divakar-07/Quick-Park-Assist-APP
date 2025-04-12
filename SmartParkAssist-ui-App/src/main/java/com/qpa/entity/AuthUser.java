package com.qpa.entity;

public class AuthUser {

    private Long authId;

    private String email;

    private String password;

    private UserInfo user;

    public AuthUser() {
    }

    public AuthUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }
}
