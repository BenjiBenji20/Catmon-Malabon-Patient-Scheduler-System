package com.azathoth.CatmonMalabonHealthCenter.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complete_name",
            length = 255, nullable = false
    )
    private String completeName;

    @OneToMany(mappedBy = "doctor") // one doctor can hold many appointments
    private final List<Appointment> appointment;

    public Doctor(Long id, String completeName, List<Appointment> appointment) {
        this.id = id;
        this.completeName = completeName;
        this.appointment = appointment;
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

    public List<Appointment> getAppointment() {
        return appointment;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", completeName='" + completeName + '\'' +
                ", appointment=" + appointment +
                '}';
    }
}
