package com.azathoth.CatmonMalabonHealthCenterSystem.repository;

import com.azathoth.CatmonMalabonHealthCenterSystem.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findPatientById(long id);

    /**
     * This method is for admin
     * @param keyword
     * @return
     */
    @Query("SELECT p FROM Patient p WHERE " +
            "LOWER(p.completeName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.gender) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.address) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Patient> searchPatient(String keyword);

    /**
     * This method is for doctor
     * @param doctorId
     * @param keyword
     * @return
     */
    @Query("SELECT p FROM Patient p JOIN p.appointment a JOIN " +
            "a.doctor d WHERE d.id = :doctorId AND " +
            "LOWER(p.completeName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.gender) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.address) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Patient> searchPatientByDoctor(Long doctorId, String keyword);

    // querying patient with gender, age and status will equal to method parameters
    @Query("SELECT p FROM Patient p JOIN p.appointment a WHERE " +
            "(:gender IS NULL OR LOWER(p.gender) = LOWER(:gender)) AND " +
            "(:age IS NULL OR p.age = :age) AND " +
            "(:status IS NULL OR LOWER(a.status) = LOWER(:status))")
    List<Patient> filterPatient(
            @Param("gender") String gender,
            @Param("age") Integer age,
            @Param("status") String status);

    @Query("SELECT p FROM Patient p JOIN p.appointment a JOIN " +
            "a.doctor d WHERE d.id = :doctorId AND " +
            "(:gender IS NULL OR LOWER(p.gender) = LOWER(:gender)) AND " +
            "(:age IS NULL OR p.age = :age) AND " +
            "(:status IS NULL OR LOWER(a.status) = LOWER(:status))")
    List<Patient> filterPatientByDoctor(
            Long doctorId,
            @Param("gender") String gender,
            @Param("age") Integer age,
            @Param("status") String status);
}
