package com.azathoth.CatmonMalabonHealthCenter.service;

import com.azathoth.CatmonMalabonHealthCenter.model.Appointment;
import com.azathoth.CatmonMalabonHealthCenter.model.Patient;
import com.azathoth.CatmonMalabonHealthCenter.model.Status;
import com.azathoth.CatmonMalabonHealthCenter.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {
    // dependencies injected via constructor
    private final PatientRepository patientRepository;
    private final AppointmentService appointmentService;

    public PatientService(PatientRepository patientRepository, AppointmentService appointmentService) {
        this.patientRepository = patientRepository;
        this.appointmentService = appointmentService;
    }

    public Optional<Patient> registerPatient(Patient newPatient) {
        // create a verification number to the new added patient
        newPatient.setVerificationNumber(getVerificationNumber());
        Patient addedPatient = patientRepository.save(newPatient);

        return Optional.of(addedPatient);
    }

    private String getVerificationNumber() {
        // creates a random letter and number
        return UUID.randomUUID().toString();
    }

}
