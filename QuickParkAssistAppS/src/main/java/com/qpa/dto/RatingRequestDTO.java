package com.qpa.dto;

public class RatingRequestDTO {
    private Integer value;

    public RatingRequestDTO() {
    }

    public RatingRequestDTO(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
