package com.azathoth.CatmonMalabonHealthCenter.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "patient_status", nullable = false)
    private Status status;

    @OneToOne
    private Patient patient;

    @ManyToOne // many appointments can be hold by one doctor
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @OneToOne(mappedBy = "appointment")
    private PatientRecord patientRecord;

    public Appointment(Long id, LocalDate scheduleDate, Status status) {
        this.id = id;
        this.scheduleDate = scheduleDate;
        this.status = status;
    }

    // for creation of new appointment
    public Appointment(){};

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
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", scheduleDate=" + scheduleDate +
                ", status=" + status +
                ", patient=" + patient +
                ", doctor=" + doctor +
                ", patientRecord=" + patientRecord +
                '}';
    }
}
