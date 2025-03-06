package com.azathoth.CatmonMalabonHealthCenterSystem.service;

import com.azathoth.CatmonMalabonHealthCenterSystem.dto.DoctorAuthenticationDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.dto.PatientDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Appointment;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Patient;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.AppointmentRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.DoctorRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, PatientRepository patientRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public Optional<?> authenticateDoctor(@Valid DoctorAuthenticationDTO doctorDto) {
        Authentication authenticateDoctor =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                doctorDto.getEmail(), doctorDto.getPassword()
                        )
                );

        if(authenticateDoctor.isAuthenticated()) {
            Doctor authDoctor = doctorRepository.findDoctorByEmail(doctorDto.getEmail());

            return Optional.of(jwtService.generateToken(authDoctor.getEmail(), authDoctor.getRole().toString()));
        }

        return Optional.empty();

    }

    public List<PatientDTO> getAllMyPatients(Long myId) {
        // get all the appointments for the doctor
        List<Appointment> appointments  = appointmentRepository.findByDoctorId(myId);

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
