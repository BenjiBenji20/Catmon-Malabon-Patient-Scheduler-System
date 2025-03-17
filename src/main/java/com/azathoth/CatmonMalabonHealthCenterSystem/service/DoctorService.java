package com.azathoth.CatmonMalabonHealthCenterSystem.service;

import com.azathoth.CatmonMalabonHealthCenterSystem.dto.DoctorAuthenticationDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.dto.DoctorDTO;
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
import com.azathoth.CatmonMalabonHealthCenterSystem.utils.AvailableDay;
import com.azathoth.CatmonMalabonHealthCenterSystem.utils.Status;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
     * The doctor entity has enforced the unique email from all its records.
     * The doctor email is extracted using jwtService.extractEmail() method.
     * This will take a jwt token and using this, it will extract its claims (email)
     * And using this extracted email, we can find the doctor from db.
     */
    public Optional<DoctorDTO> getDoctorProfile(String token) {
        try {
            // extract doctor email using jwt token
            String email = jwtService.extractEmail(token);

            // extract doctor profile by unique email
            Doctor doctor = doctorRepository.findDoctorByEmail(email);

            // if doctor didn't find
            if(doctor == null) {
                return Optional.empty();
            }

            return Optional.of(doctor)
                    .map(this::convertToDoctorDTO);
        }
        catch (ResourceNotFoundException e) {
            return Optional.empty();
        }
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

    public List<PatientDTO> getPatientsToday(LocalDate date, Long id) {
        try {
            // fetch appointments by doctor's id and param date
            List<Appointment> appointments = appointmentRepository.findAppointmentsByDateAndDoctor(date, id);

            // fetch the list of patients by appoints based on date param
            List<Patient> patients = appointments.stream()
                    .map(Appointment::getPatient).toList();

            return patients.stream()
                    .map(this::convertToPatientDTO)
                    .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            return List.of();
        }
    }

    /**
     * Passed the data of logged doctor (current doctor) to fetch and group
     * patients based on his/her available days.
     * @param doctor
     * @return
     */
    public Map<AvailableDay, List<PatientDTO>> getPatientsByAvailableDays(DoctorDTO doctor) {
        try {
            // get doctor's available days
            List<AvailableDay> availableDays = Arrays.asList(doctor.getAvailableDays());

            // get all patients
            List<Patient> allPatients = patientRepository.findAll();

            // Group patients by the day of their scheduleDate
            Map<AvailableDay, List<PatientDTO>> patientsByDay = new HashMap<>();

            for(Patient patient : allPatients) {
                // Parse the patient's scheduleDate to a LocalDate
                LocalDate scheduleDate = patient.getAppointment().getScheduleDate();

                // Convert DayOfWeek to AvailableDay enum
                DayOfWeek dayOfWeek = scheduleDate.getDayOfWeek();
                AvailableDay availableDay = AvailableDay.valueOf(dayOfWeek.name());

                // convert into patientDTO
                PatientDTO patientDTO = convertToPatientDTO(patient);

                // Check if the day matches the doctor's availability
                if(availableDays.contains(availableDay)) {
                    patientsByDay.computeIfAbsent(availableDay, k -> new ArrayList<>()).add(patientDTO);
                }
            }

            return patientsByDay;
        } catch (ResourceNotFoundException e) {
            return Map.of();
        }
    }

    /**
     *
     * @param patientId
     * @param newStatus
     * @return int values of affected by update query
     */
    @Transactional // uses transactional to tell spring that this method makes some changes to the existing db record
    public int updatePatientStatus(Long patientId, Status newStatus) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient is not found by id: " + patientId));

        if (patient.getAppointment() == null) {
            throw new IllegalStateException("Patient does not have an associated appointment.");
        }

        // Get the appointment ID
        Long appointmentId = patient.getAppointment().getId();

        // passed the appointment id associated with patient's id
        return appointmentRepository.updatePatientStatus(appointmentId, newStatus);
    }

    public Optional<List<PatientDTO>> filterPatient(Long id, String gender, Integer age, String status) {

        List<Patient> filterResult = patientRepository.filterPatientByDoctor(id, gender, age, status);

        return Optional.of(filterResult.stream()
                .map(this::convertToPatientDTO)
                .collect(Collectors.toList()));
    }

    public List<PatientDTO> searchPatient(Long id, String keyword) {
        List<Patient> patients = patientRepository.searchPatientByDoctor(id, keyword);
        return patients.stream()
                .map(this::convertToPatientDTO)
                .collect(Collectors.toList());
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

    public Optional<PatientDTO> getPatientDetails(Long id) {
        try {
            // find patient using its id
            Patient patient = patientRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found by id: " + id));

            return Optional.of(patient)
                    .map(this::convertToPatientDTO);
        } catch (ResourceNotFoundException e) {
            return Optional.empty();
        }
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

    private DoctorDTO convertToDoctorDTO(Doctor doctor) {
        return new DoctorDTO(
          doctor.getId(),
          doctor.getCompleteName(),
          doctor.getEmail(),
          doctor.getAvailableDays()
        );
    }
}
