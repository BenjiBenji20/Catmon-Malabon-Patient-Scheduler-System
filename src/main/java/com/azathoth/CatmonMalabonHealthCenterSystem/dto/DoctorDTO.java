package com.azathoth.CatmonMalabonHealthCenterSystem.dto;

import com.azathoth.CatmonMalabonHealthCenterSystem.utils.AvailableDay;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class DoctorDTO {
    private long id;

    @NotBlank(message = "Complete name cannot be empty")
    @Size(min = 2, max = 255, message = "Complete name must be between 2-255 characters")
    @Pattern(regexp = "^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}", message = "Complete name cannot contain special characters or spaces")
    private String completeName;

    @NotBlank(message = "Email cannot be empty")
    @Size(min = 2, max = 255, message = "Email must be between 100 characters")
    @Email(message = "Please use an email")
    private String email;

    @NotBlank(message = "Specify your available day/s")
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
