package com.azathoth.CatmonMalabonHealthCenter.service;

import com.azathoth.CatmonMalabonHealthCenter.model.Appointment;
import com.azathoth.CatmonMalabonHealthCenter.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenter.model.Patient;
import com.azathoth.CatmonMalabonHealthCenter.model.Role;
import com.azathoth.CatmonMalabonHealthCenter.model.utils.PatientDTO;
import com.azathoth.CatmonMalabonHealthCenter.repository.AppointmentRepository;
import com.azathoth.CatmonMalabonHealthCenter.repository.DoctorRepository;
import com.azathoth.CatmonMalabonHealthCenter.repository.PatientRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    public DoctorService(DoctorRepository doctorRepository, PasswordEncoder passwordEncoder,
                         AuthenticationManager authenticationManager, JwtService jwtService, AppointmentRepository appointmentRepository, PatientRepository patientRepository) {
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;

        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Saves a new doctor to the db
     */
    public Optional<Doctor> register(Doctor newDoctor) {
        newDoctor.setRole(Role.DOCTOR); // set role
        newDoctor.setPassword(passwordEncoder.encode(newDoctor.getPassword())); // hash password
        Doctor addedDoctor = doctorRepository.save(newDoctor); // save the new doctor to the db

        return Optional.of(addedDoctor);
    }

    /**
     * login endpoint
     */
    public Optional<?> authenticate(Doctor doctor) {
        Authentication authenticateDoctor =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                doctor.getEmail(), doctor.getPassword()
                        )
                );

        if(authenticateDoctor.isAuthenticated()) {
            Doctor authDoctor = doctorRepository.findDoctorByEmail(doctor.getEmail());

            return Optional.of(jwtService.generateToken(authDoctor.getEmail(), authDoctor.getRole().toString()));
        }

        return Optional.empty();
    }

    /**
     * Signed in doctor should pass his id here
     */
    public List<PatientDTO> getAllMyPatients(Long doctorId) {
        // get all the appointments for the doctor
        List<Appointment> appointments  = appointmentRepository.findByDoctorId(doctorId);

        // get all the patient id associated with doctor id
        List<Long> patientIds = appointments
                .stream().map(appointment ->
                        appointment.getPatient().getId())
                .toList();

        if(patientIds.isEmpty()) {
            return Collections.emptyList();
        }

        // fetch patient by collected id
        return convertAllPatientsDTO(patientIds);
    }

    private List<PatientDTO> convertAllPatientsDTO(List<Long> patientIds) {
        List<PatientDTO> patientDTOS = new ArrayList<>();

        patientIds.forEach(id -> {
            PatientDTO dto = new PatientDTO();
            Patient patient = patientRepository.findPatientById(id);
            dto.setId(patient.getId());
            dto.setCompleteName(patient.getCompleteName());
            dto.setGender(patient.getGender());
            dto.setAddress(patient.getAddress());
            dto.setAge(patient.getAge());
            dto.setContactNumber(patient.getContactNumber());
            dto.setVerificationNumber(patient.getVerificationNumber());
            dto.setScheduleDate(patient.getAppointment().getScheduleDate());
            dto.setStatus(patient.getAppointment().getStatus());

            patientDTOS.add(dto);
        });

        return patientDTOS;
    }
}
