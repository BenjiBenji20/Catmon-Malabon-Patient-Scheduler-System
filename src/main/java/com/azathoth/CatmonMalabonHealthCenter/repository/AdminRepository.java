package com.azathoth.CatmonMalabonHealthCenter.repository;

import com.azathoth.CatmonMalabonHealthCenter.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findAdminByEmail(String email);
}
