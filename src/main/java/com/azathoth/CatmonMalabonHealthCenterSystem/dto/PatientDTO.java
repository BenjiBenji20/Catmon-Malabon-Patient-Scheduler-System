package com.azathoth.CatmonMalabonHealthCenterSystem.dto;

import com.azathoth.CatmonMalabonHealthCenterSystem.model.Patient;
import com.azathoth.CatmonMalabonHealthCenterSystem.utils.Status;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class PatientDTO {
    private Long id;
    private String completeName;
    private String gender;
    private String address;
    private int age;
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

    public PatientDTO convertToDTO(Patient patient) {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(patient.getId());
        patientDTO.setCompleteName(patient.getCompleteName());
        patientDTO.setAge(patient.getAge());
        patientDTO.setGender(patient.getGender());
        patientDTO.setContactNumber(patient.getContactNumber());
        patientDTO.setAddress(patient.getAddress());
        patientDTO.setVerificationNumber(patient.getVerificationNumber());
        patientDTO.setScheduleDate(patient.getAppointment().getScheduleDate());
        patientDTO.setStatus(patient.getAppointment().getStatus());
        return patientDTO;
    }
}
