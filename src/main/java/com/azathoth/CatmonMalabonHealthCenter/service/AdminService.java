package com.azathoth.CatmonMalabonHealthCenter.service;

import com.azathoth.CatmonMalabonHealthCenter.model.*;
import com.azathoth.CatmonMalabonHealthCenter.model.utils.DoctorDTO;
import com.azathoth.CatmonMalabonHealthCenter.model.utils.PatientDTO;
import com.azathoth.CatmonMalabonHealthCenter.model.utils.UpdateDoctor;
import com.azathoth.CatmonMalabonHealthCenter.model.utils.UpdatePatient;
import com.azathoth.CatmonMalabonHealthCenter.repository.AdminRepository;
import com.azathoth.CatmonMalabonHealthCenter.repository.DoctorRepository;
import com.azathoth.CatmonMalabonHealthCenter.repository.PatientRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AdminService(PasswordEncoder passwordEncoder, AdminRepository adminRepository, AuthenticationManager authenticationManager, JwtService jwtServic, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtServic;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public Optional<Admin> createAccount(Admin admin) {
        admin.setRole(Role.ADMIN);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        Admin createdAdmin = adminRepository.save(admin);

        return Optional.of(createdAdmin);
    }

    public Optional<?> authenticate(Admin admin) {
        Authentication authenticateAdmin =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                admin.getEmail(), admin.getPassword()
                        )
                );

        if(authenticateAdmin.isAuthenticated()) {
            Admin authAdmin = adminRepository.findAdminByEmail(admin.getEmail());

            return Optional.of(jwtService.generateToken(authAdmin.getEmail(), authAdmin.getRole().toString()));
        }

        return Optional.empty();
    }

    /**
     * Update doctor endpoint
     */
    public Optional<Doctor> updateDoctor(UpdateDoctor updateDoctor) {
        Doctor existingDoctor = doctorRepository.findDoctorByEmail(updateDoctor.getExistingEmail());
        if (existingDoctor == null) {
            return Optional.empty();
        }

        existingDoctor.setCompleteName(updateDoctor.getNewCompleteName());
        existingDoctor.setPassword(passwordEncoder.encode(updateDoctor.getNewPassword()));
        existingDoctor.setAvailableDays(updateDoctor.getNewAvailableDays());

        return Optional.of(doctorRepository.save(existingDoctor));
    }

    /**
     * delete doctor endpoint
     */
    public boolean deleteDoctor(Long id) {
        Optional<Doctor> doctor = doctorRepository.findById(id);

        if(doctor.isEmpty()) {
            return false;
        }

        // delete the doctor
        doctorRepository.deleteById(id);

        return true;
    }

    /**
     * Search doctor
     */
    public List<DoctorDTO> searchDoctor(String keyword) {
        List<Doctor> doctors = doctorRepository.searchDoctor(keyword);
        return doctors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

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
     * patient requesting for update will be found using its id.
     */
    public Optional<Patient> updatePatient(UpdatePatient patient) {
        boolean isPatientExist = patientRepository.existsById(patient.getId());

        if(!isPatientExist) {
            return Optional.empty();
        }

        Patient existingPatient = patientRepository.findPatientById(patient.getId());

        existingPatient.setCompleteName(patient.getNewCompleteName());
        existingPatient.setGender(patient.getNewGender());
        existingPatient.setAddress(patient.getNewAddress());
        existingPatient.setAge(patient.getNewAge());

        return Optional.of(patientRepository.save(existingPatient));
    }

    /**
     * Delete Patient
     */
    public boolean deletePatient(Long id) {
        Optional<Patient> patient = patientRepository.findById(id);

        if(patient.isEmpty()) {
            return false;
        }

        patientRepository.deleteById(id);

        return true;
    }

    /**
     * Search Patient
     */
    public List<PatientDTO> searchPatient(String keyword) {
        List<Patient> patients = patientRepository.searchPatient(keyword);
        return patients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setCompleteName(patient.getCompleteName());
        dto.setGender(patient.getGender());
        dto.setAddress(patient.getAddress());
        dto.setAge(patient.getAge());
        dto.setContactNumber(patient.getContactNumber());
        dto.setVerificationNumber(patient.getVerificationNumber());
        dto.setScheduleDate(patient.getAppointment().getScheduleDate());
        dto.setStatus(patient.getAppointment().getStatus());
        return dto;
    }

    /**
     * Get all patients
     */
    public List<PatientDTO> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<List<PatientDTO>> filterPatient(String gender, Integer age, String status) {
        List<Patient> filterResult = patientRepository.filterPatient(gender, age, status);

        return Optional.of(filterResult.stream()
                            .map(this::convertToDTO)
                            .collect(Collectors.toList()));
    }
}
