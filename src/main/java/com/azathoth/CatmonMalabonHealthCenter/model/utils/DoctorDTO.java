package com.azathoth.CatmonMalabonHealthCenter.model.utils;

import com.azathoth.CatmonMalabonHealthCenter.model.AvailableDay;

public class DoctorDTO {
    private long id;
    private String completeName;
    private String email;
    private AvailableDay[] availableDays;

    public DoctorDTO(long id, String completeName, String email, AvailableDay[] availableDays) {
        this.id = id;
        this.completeName = completeName;
        this.email = email;
        this.availableDays = availableDays;
    }

    public DoctorDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompleteName() {
        return completeName;
    }

    public void setCompleteName(String completeName) {
        this.completeName = completeName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AvailableDay[] getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(AvailableDay[] availableDays) {
        this.availableDays = availableDays;
    }
}
