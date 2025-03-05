package com.azathoth.CatmonMalabonHealthCenterSystem.service;

import com.azathoth.CatmonMalabonHealthCenterSystem.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.PendingDoctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.PendingDoctorRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PendingDoctorService {
    private final PendingDoctorRepository pendingDoctorRepository;

    public PendingDoctorService(PendingDoctorRepository pendingDoctorRepository) {
        this.pendingDoctorRepository = pendingDoctorRepository;
    }

    // save to pending doctor table. waiting to be accepted by admin
    public Optional<PendingDoctor> requestRegister(@Valid PendingDoctor requestRegistration) {
        try {
            return Optional.of(pendingDoctorRepository.save(requestRegistration));
        }
        catch (NullPointerException e) {
            return Optional.empty();
        }
    }
}
