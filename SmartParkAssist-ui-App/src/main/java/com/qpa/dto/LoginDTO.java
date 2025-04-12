package com.qpa.dto;

public class LoginDTO {
	private String email;
	private String password;

	// Default constructor
	public LoginDTO() {
	}

	// Parameterized constructor
	public LoginDTO(String email, String password) {
		this.email = email;
		this.password = password;
	}

	// Getters & Setters
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
}
