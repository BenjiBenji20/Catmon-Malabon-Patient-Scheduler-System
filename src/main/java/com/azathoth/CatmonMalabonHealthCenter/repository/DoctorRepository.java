package com.azathoth.CatmonMalabonHealthCenter.repository;

import com.azathoth.CatmonMalabonHealthCenter.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
