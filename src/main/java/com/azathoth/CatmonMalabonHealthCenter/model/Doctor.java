package com.azathoth.CatmonMalabonHealthCenter.model;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complete_name",
            length = 255, nullable = false
    ) private String completeName;

    @Column(name = "email_address", nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    /**
     * this entity column will create a new collection table which linked to the
     * doctor entity using its id named it as doctor_id.
     * This column is an array storing enum values of doctor's selected
     * available days.
     */
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "doctor_available_days", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "available_day")
    private AvailableDay[] availableDays;

    @OneToMany(mappedBy = "doctor") // one doctor can hold many appointments
    private List<Appointment> appointment;

    public Doctor(Long id, String completeName, String email, String password, AvailableDay[] availableDays, List<Appointment> appointment) {
        this.id = id;
        this.completeName = completeName;
        this.email = email;
        this.password = password;
        this.availableDays = availableDays;
        this.appointment = appointment;
    }

    protected Doctor() {}

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

    public List<Appointment> getAppointment() {
        return appointment;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", completeName='" + completeName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", availableDays=" + Arrays.toString(availableDays) +
                ", appointment=" + appointment +
                '}';
    }
}
