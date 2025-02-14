package com.azathoth.CatmonMalabonHealthCenter.model;

import jakarta.persistence.*;
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
    private Date dataAt;

    @Column(name = "complete_name",
        length = 255, nullable = false
    )
    private String completeName;

    @Column(nullable = false)
    private String gender;

    @Column(name = "complete_address", length = 400, nullable = false)
    private String address;

    @Column(length = 3, nullable = false)
    private int age;

    @Column(name = "contact_no", length = 12, nullable = false)
    private String contactNumber;

    @Column(name = "verification_no", nullable = false)
    private String verificationNumber;

    @OneToOne(mappedBy = "patient")
    private final Appointment appointment;

    // constructor injected
    public Patient(Appointment appointment, String verificationNumber, String contactNumber, int age, String address, String gender, String completeName, Date dataAt, Long id) {
        this.appointment = appointment;
        this.verificationNumber = verificationNumber;
        this.contactNumber = contactNumber;
        this.age = age;
        this.address = address;
        this.gender = gender;
        this.completeName = completeName;
        this.dataAt = dataAt;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataAt() {
        return dataAt;
    }

    public void setDataAt(Date dataAt) {
        this.dataAt = dataAt;
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
