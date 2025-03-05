package com.azathoth.CatmonMalabonHealthCenterSystem.dto;

import com.azathoth.CatmonMalabonHealthCenterSystem.utils.Status;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class PatientDTO {
    private Long id;

    @NotBlank(message = "Complete name cannot be empty")
    @Size(min = 2, max = 255, message = "Complete name must be between 2-255 characters")
    @Pattern(regexp = "^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}", message = "Complete name cannot contain special characters or spaces")
    private String completeName;

    @NotBlank(message = "Gender cannot be empty")
    @Size(min = 1, max = 10, message = "Gender must be 1-10 characters")
    private String gender;

    @NotBlank(message = "Address cannot be empty")
    @Size(max = 255)
    private String address;

    @NotNull(message = "Age cannot be null")
    @Min(value = 0, message = "Age must be a positive number")
    private int age;

    @NotBlank(message = "Please provide your contact number")
    @Size(max = 13)
    private String contactNumber;

    private String verificationNumber;

    @NotNull(message = "Schedule date cannot be null")
    @Future(message = "Date must be in future")
    private LocalDate scheduleDate;

    @NotBlank(message = "Please provide your status")
    private Status status;

    public PatientDTO(Long id, String completeName, String gender, String address, int age, String contactNumber, String verificationNumber, LocalDate scheduleDate, Status status) {
        this.id = id;
        this.completeName = completeName;
        this.gender = gender;
        this.address = address;
        this.age = age;
        this.contactNumber = contactNumber;
        this.verificationNumber = verificationNumber;
        this.scheduleDate = scheduleDate;
        this.status = status;
    }

    public PatientDTO() {
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

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
