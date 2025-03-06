package com.azathoth.CatmonMalabonHealthCenterSystem.dto;

public class UpdatePatientDTO {
    private Long id;
    private String newCompleteName;
    private String newGender;
    private String newAddress;
    private int newAge;

    public UpdatePatientDTO(String newCompleteName, String newGender, String newAddress, int newAge) {
        this.newCompleteName = newCompleteName;
        this.newGender = newGender;
        this.newAddress = newAddress;
        this.newAge = newAge;
    }

    public UpdatePatientDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNewCompleteName() {
        return newCompleteName;
    }

    public void setNewCompleteName(String newCompleteName) {
        this.newCompleteName = newCompleteName;
    }

    public String getNewGender() {
        return newGender;
    }

    public void setNewGender(String newGender) {
        this.newGender = newGender;
    }

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }

    public int getNewAge() {
        return newAge;
    }

    public void setNewAge(int newAge) {
        this.newAge = newAge;
    }
}
