package com.azathoth.CatmonMalabonHealthCenterSystem.service;

import com.azathoth.CatmonMalabonHealthCenterSystem.dto.DoctorAuthenticationDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.dto.PatientDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.exception.ResourceNotFoundException;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Appointment;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Patient;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.PatientRecord;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.AppointmentRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.DoctorRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.PatientRecordRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.PatientRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.utils.Status;
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
    private final PatientRecordRepository patientRecordRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, PatientRepository patientRepository, PatientRecordRepository patientRecordRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.patientRecordRepository = patientRecordRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * login endpoint
     */
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

    /**
     * Signed in doctor should pass his id here
     */
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


    public Optional<PatientDTO> updatePatientStatus(Long patientId, Status newStatus) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient is not found by id: " + patientId));

        patient.getAppointment().setStatus(newStatus);

        Patient updatedPatient = patientRepository.save(patient);

        PatientDTO patientDTO = convertToDTO(updatedPatient);

        return Optional.of(patientDTO);
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
     * pass json value pair: attended:boolean, prescription, string, diagnosis: string
     */
    public Optional<PatientRecord> createPatientRecord(Long patientId, PatientRecord record) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient is not found by id: " + patientId));

        Appointment appointment = patient.getAppointment();

        if (appointment == null) {
            return Optional.empty();
        }

        PatientRecord patientRecord = patientRecordRepository.findAppointmentById(patient.getAppointment().getId());
        if(patientRecord == null) {
            patientRecord = new PatientRecord(
                    appointment,
                    record.isAttended(),
                    record.getPrescription(),
                    record.getDiagnosis(),
                    null
            );
            // Set the bidirectional relationship
            appointment.setPatientRecord(patientRecord);
        }

        patientRecord.setAttended(record.isAttended());
        patientRecord.setDiagnosis(record.getDiagnosis());
        patientRecord.setPrescription(record.getPrescription());

        PatientRecord recorded = patientRecordRepository.save(patientRecord);

        return Optional.of(recorded);
    }
}
