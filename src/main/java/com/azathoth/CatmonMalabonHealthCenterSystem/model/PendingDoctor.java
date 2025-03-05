package com.azathoth.CatmonMalabonHealthCenterSystem.model;

import com.azathoth.CatmonMalabonHealthCenterSystem.utils.AvailableDay;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "pending_doctor",
    uniqueConstraints = {
            @UniqueConstraint(columnNames = "complete_name"),
            @UniqueConstraint(columnNames = "email_address")
    }
)
public class PendingDoctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Complete name cannot be empty")
    @Size(min = 2, max = 255, message = "Complete name must be between 2-255 characters")
    @Pattern(regexp = "^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}", message = "Complete name cannot contain special characters or spaces")
    @Column(name = "complete_name",length = 255, nullable = false)
    private String completeName;

    @NotBlank(message = "Email cannot be empty")
    @Size(min = 2, max = 255, message = "Email must be between 100 characters")
    @Email(message = "Please use an email")
    @Column(name = "email_address", nullable = false)
    private String email;

    @NotBlank(message = "Please provide a password")
    @Size(min = 8, max = 255, message = "Please provide a strong password")
    @Column(nullable = false)
    private String password;

    /**
     * this entity column will create a new collection table which linked to the
     * doctor entity using its id named it as doctor_id.
     * This column is an array storing enum values of doctor's selected
     * available days.
     */
    @NotEmpty(message = "Specify your available day/s")
    @Enumerated(EnumType.STRING)
    @Column(name = "available_day")
    private AvailableDay[] availableDays;

    public PendingDoctor(Long id, String completeName, String email, String password, AvailableDay[] availableDays) {
        this.id = id;
        this.completeName = completeName;
        this.email = email;
        this.password = password;
        this.availableDays = availableDays;
    }

    public PendingDoctor() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AvailableDay[] getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(AvailableDay[] availableDays) {
        this.availableDays = availableDays;
    }
}
