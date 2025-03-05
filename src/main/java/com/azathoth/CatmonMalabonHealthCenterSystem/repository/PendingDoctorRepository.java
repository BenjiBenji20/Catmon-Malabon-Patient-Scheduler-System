package com.azathoth.CatmonMalabonHealthCenterSystem.repository;

import com.azathoth.CatmonMalabonHealthCenterSystem.model.PendingDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingDoctorRepository extends JpaRepository<PendingDoctor, Long> {
}
