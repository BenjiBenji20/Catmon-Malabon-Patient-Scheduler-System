package com.azathoth.CatmonMalabonHealthCenterSystem.repository;

import com.azathoth.CatmonMalabonHealthCenterSystem.utils.AvailableDay;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    /**
     * Loop through
     */
    @Query("SELECT d FROM Doctor d WHERE :day MEMBER OF d.availableDays")
    List<Doctor> findDoctorsByAvailableDay(@Param("day")AvailableDay day);

    Doctor findDoctorByEmail(String email);

    @Query("SELECT d from Doctor d WHERE " +
            "LOWER(d.completeName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Doctor> searchDoctor(String keyword);
}
