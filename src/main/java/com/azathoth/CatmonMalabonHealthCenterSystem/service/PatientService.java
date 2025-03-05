package com.azathoth.CatmonMalabonHealthCenterSystem.service;

import com.azathoth.CatmonMalabonHealthCenterSystem.dto.PatientDTO;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenterSystem.model.Patient;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.DoctorRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.repository.PatientRepository;
import com.azathoth.CatmonMalabonHealthCenterSystem.utils.AvailableDay;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {
    // twilio api keys
    @Value("${twilio.acc.sid}")
    private String twilioSID;
    @Value("${twilio.auth.key}")
    private String twilioKey;
    @Value("${twilio.my.phone.number}")
    private String myPhoneNumber;

    // dependencies injected via constructor
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    public PatientService(PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    /**
     * This service endpoint saves a new patient using its repository.
     * 1st, the parameter has only values of completeName, address,
     * gender, contactNumber and age so this parameter value will throw
     * a null pointer exception not filling its Patient constructor parameter.
     * 2nd, the new patient will set its verification number using getVerificationNumber method and
     * send through sms message to the specified patient cp number.
     * 3rd, checks for null value of appointment, if null then create a new appointment with this new patient.
     * 4th, get patients selected schedule YYYY-MM-DD, convert the date into day ex. monday, tuesday...
     * convert the date into day ex. monday, tuesday...find doctor with the same schedule with patient
     * then set a doctor to the new patient by finding compatible day schedule.
     * 5th, save the patient to the database using repository
     *
     */
        public Optional<PatientDTO> registerPatient(@Valid Patient newPatient) {
            try {
                // create a verification number to the new added patient
                newPatient.setVerificationNumber(getVerificationNumber());

                // configured twilio commented for a moment :>
    //            Twilio.init(twilioSID, twilioKey);
    //            Message.creator(
    //                    new PhoneNumber("+63" + newPatient.getContactNumber()), // to
    //                    new PhoneNumber(myPhoneNumber), // from
    //                    "From Catmon Health Center this is your confirmation code: " + newPatient.getVerificationNumber() // body (message)
    //            ).create();

                // if patient doesn't have an appointment, then set an appointment
                if (newPatient.getAppointment() != null) {
                    newPatient.getAppointment().setPatient(newPatient);
                }
                // get patients selected schedule YYYY-MM-DD
                LocalDate patientScheduleDate = newPatient.getAppointment().getScheduleDate();
                // date today as of patient registration
                boolean isDateFromPast = compareDate(patientScheduleDate);
                if(isDateFromPast) {
                    return Optional.empty();
                }

                // convert the date into day ex. monday, tuesday...
                DayOfWeek patientSelectedDay = patientScheduleDate.getDayOfWeek();
                // convert the date into day ex. monday, tuesday... from enum
                AvailableDay doctorScheduleDay = AvailableDay.valueOf(patientSelectedDay.toString());
                // find doctors with the same schedule with patient
                List<Doctor> availableDoctors = doctorRepository.findDoctorsByAvailableDay(doctorScheduleDay);
                // If no doctors are available, return an error
                if (availableDoctors.isEmpty()) {
                    logger.warn("No doctors available on the selected day: {}", doctorScheduleDay);
                    return Optional.empty(); // Or throw a custom exception
                }

                // Assign the first available doctor to the appointment
                newPatient.getAppointment().setDoctor(availableDoctors.getFirst());

                // Save the patient to the database
                Patient addedPatient = patientRepository.save(newPatient);

                // convert to dto
                PatientDTO patientDTO = new PatientDTO();

                return Optional.of(patientDTO.convertToDTO(addedPatient));
            }
            catch (NullPointerException e) {
                logger.error("NullPointerException occurred: {}", e.getMessage());
                return Optional.empty();
            }
            catch (Exception e) {
                logger.error("Unexpected error occurred: {}", e.getMessage());
                return Optional.empty();
            }
        }

    private String getVerificationNumber() {
        // creates a random letter and number
        return UUID.randomUUID().toString();
    }

    private boolean compareDate(LocalDate dateSchedule) {
        LocalDate today = LocalDate.now();

        return dateSchedule.isBefore(today);
    }
}
