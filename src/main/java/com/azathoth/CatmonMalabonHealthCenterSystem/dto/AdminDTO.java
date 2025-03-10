package com.azathoth.CatmonMalabonHealthCenterSystem.dto;

import com.azathoth.CatmonMalabonHealthCenterSystem.model.Admin;
import com.azathoth.CatmonMalabonHealthCenterSystem.utils.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AdminDTO {
    private Long id;

    @NotBlank(message = "Admin name cannot be empty")
    @Size(min = 2, max = 255, message = "Admin name must be between 2-255 characters")
    @Pattern(regexp = "^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}", message = "Admin name cannot contain special characters or spaces")
    private String adminName;

    @NotBlank(message = "Email cannot be empty")
    @Size(min = 2, max = 255, message = "Email must be between 100 characters")
    @Email(message = "Please use an email")
    private String email;

    @NotBlank(message = "Please provide a password")
    @Size(min = 8, max = 255, message = "Please provide a strong password")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public AdminDTO(Long id, String adminName, String email, String password, Role role) {
        this.id = id;
        this.adminName = adminName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public AdminDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Admin convertToAdminEntity(AdminDTO adminDTO) {
        Admin admin = new Admin();

        admin.setAdminName(adminDTO.getAdminName());
        admin.setEmail(adminDTO.getEmail());
        admin.setRole(Role.ADMIN);
        admin.setPassword(adminDTO.getPassword());

        return admin;
    }
}
