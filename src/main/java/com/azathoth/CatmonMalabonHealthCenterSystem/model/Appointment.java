package com.azathoth.CatmonMalabonHealthCenterSystem.model;

import com.azathoth.CatmonMalabonHealthCenterSystem.utils.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Schedule date cannot be null")
    @Future(message = "Date must be in future")
    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "patient_status", nullable = false)
    private Status status;

    @OneToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private Patient patient;

    @ManyToOne // many appointments can be hold by one doctor
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @OneToOne(mappedBy = "appointment")
    private PatientRecord patientRecord;

    public Appointment(Long id, LocalDate scheduleDate, Status status, Doctor doctor, Patient patient) {
        this.id = id;
        this.scheduleDate = scheduleDate;
        this.status = status;
        this.patient = patient;
        this.doctor = doctor;
    }

    // for creation of new appointment
    protected Appointment(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public PatientRecord getPatientRecord() {
        return patientRecord;
    }

    public void setPatientRecord(PatientRecord patientRecord) {
        this.patientRecord = patientRecord;
        if(patientRecord == null) {
            patientRecord.setAppointment(this);
        }
    }
}
