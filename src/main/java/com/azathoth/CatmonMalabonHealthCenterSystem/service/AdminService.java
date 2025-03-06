package com.azathoth.CatmonMalabonHealthCenterSystem.service;

import com.azathoth.CatmonMalabonHealthCenterSystem.dto.AdminAuthenticationDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.dto.AdminDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.dto.DoctorDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.dto.UpdateDoctorDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.exception.ResourceNotFoundException;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Admin;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.PendingDoctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.AdminRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.DoctorRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.PendingDoctorRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.utils.Role;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final PendingDoctorRepository pendingDoctorRepository;
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AdminService(PendingDoctorRepository pendingDoctorRepository, DoctorRepository doctorRepository, AdminRepository adminRepository, PasswordEncoder encoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.pendingDoctorRepository = pendingDoctorRepository;
        this.doctorRepository = doctorRepository;
        this.adminRepository = adminRepository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public Optional<Admin> createAccount(@Valid AdminDTO adminDTO) {
        Admin admin = adminDTO.convertToAdminEntity(adminDTO);

        admin.setPassword(encoder.encode(admin.getPassword()));

        // save created admin
        return Optional.of(adminRepository.save(admin));
    }

    public Optional<?> authenticate(@Valid AdminAuthenticationDTO adminAuthenticationDTO) {
        Authentication authenticateAdmin =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                adminAuthenticationDTO.getEmail(), adminAuthenticationDTO.getPassword()
                        )
                );

        if(authenticateAdmin.isAuthenticated()) {
            Admin authAdmin = adminRepository.findAdminByEmail(adminAuthenticationDTO.getEmail());

            return Optional.of(jwtService.generateToken(authAdmin.getEmail(), authAdmin.getRole().toString()));
        }

        return Optional.empty();
    }

    public Optional<Doctor> acceptDoctorRequest(Long requestId) {
        try {
            Optional<PendingDoctor> requestingDoctor = pendingDoctorRepository.findById(requestId);

            if(requestingDoctor.isPresent()) {
                PendingDoctor pendingDoctor = requestingDoctor.get();
                // pass pending doctor's information to the doctor entity
                // appointment field will be assign in patient service
                Doctor newDoctor = new Doctor();
                newDoctor.setCompleteName(pendingDoctor.getCompleteName());
                newDoctor.setEmail(pendingDoctor.getEmail());
                newDoctor.setPassword(pendingDoctor.getPassword()); // password is already hashed in pending doctor service
                newDoctor.setAvailableDays(pendingDoctor.getAvailableDays());
                newDoctor.setRole(Role.DOCTOR); // assign a role
                // save a new doctor
                Doctor addedDoctor = doctorRepository.save(newDoctor);
                // delete the newly added doctor from pending table
                pendingDoctorRepository.delete(pendingDoctor);
                // pass doctor obj as response to be use by websocket channel
                return Optional.of(addedDoctor);
            }

            return Optional.empty();
        }
        catch (HttpClientErrorException.NotFound notFound) {
            logger.error("Pending doctor is not available by id: {}", notFound.getMessage());
            return Optional.empty();
        }
    }

    /**
     * update doctor endpoint
     */
    public UpdateDoctorDTO updateDoctor(Long doctorExistingId, UpdateDoctorDTO doctorDTO) {
        Doctor doctor = doctorRepository.findById(doctorExistingId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorExistingId));

        // Map fields from DTO to Entity
        doctor.setCompleteName(doctorDTO.getNewCompleteName());
        doctor.setPassword(encoder.encode(doctorDTO.getNewPassword()));
        doctor.setAvailableDays(doctorDTO.getNewAvailableDays());

        Doctor updatedDoctor = doctorRepository.save(doctor);

        // Convert back to DTO before returning
        return new UpdateDoctorDTO(
                updatedDoctor.getCompleteName(),
                updatedDoctor.getPassword(),
                updatedDoctor.getAvailableDays()
        );
    }

    /**
     * delete doctor endpoint
     */
    public Optional<?> deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        // delete the doctor
        doctorRepository.deleteById(id);

        return Optional.of(true);
    }

    private DoctorDTO convertToDTO(Doctor doctor) {
        DoctorDTO doctorDTO = new DoctorDTO();

        doctorDTO.setId(doctor.getId());
        doctorDTO.setCompleteName(doctor.getCompleteName());
        doctorDTO.setEmail(doctor.getEmail());
        doctorDTO.setAvailableDays(doctor.getAvailableDays());

        return doctorDTO;
    }

    /**
     * Get all doctors
     */
    public List<DoctorDTO> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * SEARCH doctor
     */
    public List<DoctorDTO> searchDoctor(String keyword) {
        List<Doctor> doctors = doctorRepository.searchDoctor(keyword);
        return doctors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
