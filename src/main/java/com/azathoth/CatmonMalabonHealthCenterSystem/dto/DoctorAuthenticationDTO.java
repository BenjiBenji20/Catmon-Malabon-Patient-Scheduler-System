package com.azathoth.CatmonMalabonHealthCenterSystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DoctorAuthenticationDTO {
    @NotBlank(message = "Email cannot be empty")
    @Size(min = 2, max = 255, message = "Email must be between 100 characters")
    @Email(message = "Please use an email")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 255, message = "Password must be between 8-255 characters")
    private String password;

    public DoctorAuthenticationDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public DoctorAuthenticationDTO() {
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
}
