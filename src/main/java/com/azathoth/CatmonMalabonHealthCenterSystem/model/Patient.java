package com.azathoth.CatmonMalabonHealthCenterSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date dateAt;

    @NotBlank(message = "Complete name cannot be empty")
    @Size(min = 2, max = 255, message = "Complete name must be between 2-255 characters")
    @Pattern(regexp = "^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}", message = "Complete name cannot contain special characters")
    @Column(name = "complete_name", length = 255, nullable = false)
    private String completeName;

    @NotBlank(message = "Gender cannot be empty")
    @Size(min = 1, max = 10, message = "Gender must be 1-10 characters")
    @Column(nullable = false)
    private String gender;

    @NotBlank(message = "Address cannot be empty")
    @Size(max = 255)
    @Column(name = "complete_address", length = 255, nullable = false)
    private String address;

    @Min(value = 0, message = "Age must be a positive number")
    @Column(length = 3, nullable = false)
    private int age;

    @NotBlank(message = "Please provide your contact number")
    @Size(max = 13)
    @Column(name = "contact_no", length = 12, nullable = false)
    private String contactNumber;

    @Column(name = "verification_no", nullable = false)
    private String verificationNumber;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonIgnore // ignore appointment record in json response
    private Appointment appointment;

    public Patient(Long id, Date dateAt, String completeName, int age, String gender,
                   String contactNumber, String address, Appointment appointment) {
        this.id = id;
        this.dateAt = dateAt;
        this.completeName = completeName;
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
        this.appointment = appointment;

        if(appointment != null) appointment.setPatient(this);
    }

    protected Patient(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateAt() {
        return dateAt;
    }

    public void setDateAt(Date dateAt) {
        this.dateAt = dateAt;
    }

    public String getCompleteName() {
        return completeName;
    }

    public void setCompleteName(String completeName) {
        this.completeName = completeName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getVerificationNumber() {
        return verificationNumber;
    }

    public void setVerificationNumber(String verificationNumber) {
        this.verificationNumber = verificationNumber;
    }

    public Appointment getAppointment() {
        return appointment;
    }
}
