package com.azathoth.CatmonMalabonHealthCenter.repository;

import com.azathoth.CatmonMalabonHealthCenter.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findPatientById(long id);

    @Query("SELECT p FROM Patient p WHERE " +
            "LOWER(p.completeName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.gender) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.address) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Patient> searchPatient(String keyword);
}
