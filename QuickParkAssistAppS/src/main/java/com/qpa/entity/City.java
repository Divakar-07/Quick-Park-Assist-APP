package com.qpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "pincode", nullable = false)
    private String pincode;

    @Column(name = "state_name", nullable = false)
    private String stateName;

    // Constructors
    public City() {}

    public City(Long id, String name, String pincode, String stateName) {
        this.id = id;
        this.name = name;
        this.pincode = pincode;
        this.stateName = stateName;
    }

    public City(String name, String pincode, String stateName) {
        this.name = name;
        this.pincode = pincode;
        this.stateName = stateName;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
