package com.azathoth.CatmonMalabonHealthCenter.repository;

import com.azathoth.CatmonMalabonHealthCenter.model.Patient;
import com.azathoth.CatmonMalabonHealthCenter.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // querying patient with gender, age and status will equal to method parameters
    @Query("SELECT p FROM Patient p JOIN p.appointment a WHERE " +
            "(:gender IS NULL OR LOWER(p.gender) = LOWER(:gender)) AND " +
            "(:age IS NULL OR p.age = :age) AND " +
            "(:status IS NULL OR LOWER(a.status) = LOWER(:status))")
    List<Patient> filterPatient(
            @Param("gender") String gender,
            @Param("age") Integer age,
            @Param("status") String status);
}
