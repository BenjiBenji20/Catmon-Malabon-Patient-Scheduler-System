package com.azathoth.CatmonMalabonHealthCenterSystem.service;

import com.azathoth.CatmonMalabonHealthCenterSystem.dto.*;
import com.azathoth.CatmonMalabonHealthCenterSystem.exception.ResourceNotFoundException;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.*;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.*;
import com.azathoth.CatmonMalabonHealthCenterSystem.utils.Role;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AdminService(PendingDoctorRepository pendingDoctorRepository, DoctorRepository doctorRepository, AdminRepository adminRepository, PatientRepository patientRepository, AppointmentRepository appointmentRepository, PasswordEncoder encoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.pendingDoctorRepository = pendingDoctorRepository;
        this.doctorRepository = doctorRepository;
        this.adminRepository = adminRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
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
        try {
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
        catch (AuthenticationException e) {
            return Optional.empty();
        }
    }

    /**
     * The admin entity has enforced the unique email from all its records.
     * The admin email is extracted using jwtService.extractEmail() method.
     * This will take a jwt token and using this, it will extract its claims (email)
     * And using this extracted email, we can find the admin from db.
     */
    public Optional<AdminDTO> getAdminProfile(String token) {
        try {
            // extract admin email from the token
            String email = jwtService.extractEmail(token);

            // find the admin from db using its email
            Admin adminProfile = adminRepository.findAdminByEmail(email);

            // return empty object if admin does not found
            if(adminProfile == null) {
                return Optional.empty();
            }

            return Optional.of(adminProfile)
                    .map(this::convertToAdminDTO);
        }
        catch (HttpClientErrorException.NotFound notFound) {
            logger.error("Admin does not found by email: {}", jwtService.extractEmail(token));
            return Optional.empty();
        }
    }

    public List<AdminDTO> getAllCoAdmins() {
        List<Admin> coAdmins = adminRepository.findAll();

        return coAdmins.stream()
                .map(this::convertToAdminDTO)
                .collect(Collectors.toList());
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

    public boolean deleteDoctorRequest(Long id) {
        try {
            Optional<PendingDoctor> requestingDoctor = pendingDoctorRepository.findById(id);

            if(requestingDoctor.isPresent()) {
                pendingDoctorRepository.delete(requestingDoctor.get());
                return true;
            }

            return false;
        }
        catch (HttpClientErrorException.NotFound notFound) {
            return false;
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

        doctorRepository.save(doctor);

        // Convert back to DTO before returning
        return new UpdateDoctorDTO(
                doctor.getCompleteName(),
                doctor.getPassword(),
                doctor.getAvailableDays()
        );
    }

    /**
     * delete doctor endpoint
     */
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        // delete the doctor
        doctorRepository.deleteById(id);
    }

    /**
     * SEARCH doctor
     */
    public List<DoctorDTO> searchDoctor(String keyword) {
        List<Doctor> doctors = doctorRepository.searchDoctor(keyword);
        return doctors.stream()
                .map(this::convertToDoctorDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all doctors
     */
    public List<DoctorDTO> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream()
                .map(this::convertToDoctorDTO)
                .collect(Collectors.toList());
    }

    public UpdatePatientDTO updatePatient(Long id, UpdatePatientDTO patient) {
        Patient patientToBeUpdate = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        // Map fields from DTO to Entity
        patientToBeUpdate.setCompleteName(patient.getNewCompleteName());
        patientToBeUpdate.setGender(patient.getNewGender());
        patientToBeUpdate.setAddress(patient.getNewAddress());
        patientToBeUpdate.setAge(patient.getNewAge());

        patientRepository.save(patientToBeUpdate);

        // Convert back to DTO before returning
        return new UpdatePatientDTO(
                patientToBeUpdate.getCompleteName(),
                patientToBeUpdate.getGender(),
                patientToBeUpdate.getAddress(),
                patientToBeUpdate.getAge()
        );
    }

    /**
     * Get all patients
     */
    public List<PatientDTO> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(this::convertToPatientDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete a patient by ID
     */
    public void deletePatient(Long id) {
        // Check if the patient exists
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        // Delete the patient
        patientRepository.deleteById(id);
    }

    /**
     * TABLE CONTROLLER TO FILTER PATIENT TABLE
     */
    public Optional<List<PatientDTO>> filterPatient(String gender, Integer age, String status) {
        List<Patient> filterResult = patientRepository.filterPatient(gender, age, status);

        return Optional.of(filterResult.stream()
                .map(this::convertToPatientDTO)
                .collect(Collectors.toList()));
    }

    /**
     * Get all pending doctor record from db
     */
    public List<PendingDoctorDTO> getAllPendingDoctors() {
        List<PendingDoctor> pendingDoctors = pendingDoctorRepository.findAll();

        return pendingDoctors.stream()
                .map(this::convertToPendingDoctorDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all appointment record from db
     */
    public List<AppointmentDTO> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();

        return appointments.stream()
                .map(this::convertToAppointmentDTO)
                .collect(Collectors.toList());
    }

    private AdminDTO convertToAdminDTO(Admin admin) {
        AdminDTO adminDTO = new AdminDTO();

        adminDTO.setId(admin.getId());
        adminDTO.setAdminName(admin.getAdminName());
        adminDTO.setEmail(admin.getEmail());

        return adminDTO;
    }

    private DoctorDTO convertToDoctorDTO(Doctor doctor) {
        DoctorDTO doctorDTO = new DoctorDTO();

        doctorDTO.setId(doctor.getId());
        doctorDTO.setCompleteName(doctor.getCompleteName());
        doctorDTO.setEmail(doctor.getEmail());
        doctorDTO.setAvailableDays(doctor.getAvailableDays());

        return doctorDTO;
    }

    private PatientDTO convertToPatientDTO(Patient patient) {
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

    private PendingDoctorDTO convertToPendingDoctorDTO(PendingDoctor pendingDoctor) {
        PendingDoctorDTO pendingDoctorDTO = new PendingDoctorDTO();
        pendingDoctorDTO.setId(pendingDoctor.getId());
        pendingDoctorDTO.setCompleteName(pendingDoctor.getCompleteName());
        pendingDoctorDTO.setEmail(pendingDoctor.getEmail());

        return pendingDoctorDTO;
    }

    private AppointmentDTO convertToAppointmentDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setScheduleDate(appointment.getScheduleDate());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setPatientName(appointment.getPatient().getCompleteName());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setDoctorName(appointment.getDoctor().getCompleteName());
        dto.setStatus(appointment.getStatus());

        return dto;
    }
}
