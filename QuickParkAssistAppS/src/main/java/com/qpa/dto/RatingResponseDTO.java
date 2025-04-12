package com.qpa.dto;

public class RatingResponseDTO {
    private Long id;
    private Integer value;
    private String username;

    public RatingResponseDTO() {
    }

    public RatingResponseDTO(Long id, Integer value, String username) {
        this.id = id;
        this.value = value;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
