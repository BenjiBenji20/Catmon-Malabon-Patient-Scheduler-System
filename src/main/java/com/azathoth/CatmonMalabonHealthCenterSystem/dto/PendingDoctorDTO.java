package com.azathoth.CatmonMalabonHealthCenterSystem.dto;

public class PendingDoctorDTO {
    private Long id;
    private String completeName;
    private String email;

    public PendingDoctorDTO(Long id, String completeName, String email) {
        this.id = id;
        this.completeName = completeName;
        this.email = email;
    }

    public PendingDoctorDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
}
