package com.azathoth.CatmonMalabonHealthCenter.model;

import jakarta.persistence.*;

@Entity
@Table(name = "patient_record")
public class PatientRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String diagnosis;

    @Column(length = 1000)
    private String prescription;

    @Column(name = "is_attended")
    private boolean isAttended;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private final Appointment appointment;

    public PatientRecord(Appointment appointment, boolean isAttended, String prescription, String diagnosis, Long id) {
        this.id = id;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.isAttended = isAttended;
        this.appointment = appointment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public boolean isAttended() {
        return isAttended;
    }

    public void setAttended(boolean attended) {
        isAttended = attended;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    @Override
    public String toString() {
        return "PatientRecord{" +
                "id=" + id +
                ", diagnosis='" + diagnosis + '\'' +
                ", prescription='" + prescription + '\'' +
                ", isAttended=" + isAttended +
                ", appointment=" + appointment +
                '}';
    }
}
