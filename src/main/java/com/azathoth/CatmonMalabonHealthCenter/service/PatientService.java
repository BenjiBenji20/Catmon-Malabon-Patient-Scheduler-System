package com.azathoth.CatmonMalabonHealthCenter.service;

import com.azathoth.CatmonMalabonHealthCenter.model.AvailableDay;
import com.azathoth.CatmonMalabonHealthCenter.model.Doctor;
import com.azathoth.CatmonMalabonHealthCenter.model.Patient;
import com.azathoth.CatmonMalabonHealthCenter.repository.DoctorRepository;
import com.azathoth.CatmonMalabonHealthCenter.repository.PatientRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {
    // dependencies injected via constructor
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    // twilio api keys
    @Value("${twilio.acc.sid}")
    private String twilioSID;
    @Value("${twilio.auth.key}")
    private String twilioKey;
    @Value("${twilio.my.phone.number}")
    private String myPhoneNumber;

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
    public Optional<Patient> registerPatient(Patient newPatient) {
        // create a verification number to the new added patient
        newPatient.setVerificationNumber(getVerificationNumber());

        // configured twilio commented for a moment :>
        Twilio.init(twilioSID, twilioKey);
        Message.creator(
                new PhoneNumber("+63" + newPatient.getContactNumber()), // to
                new PhoneNumber(myPhoneNumber), // from
                "From Catmon Health Center this is your confirmation code: " + newPatient.getVerificationNumber() // body (message)
        ).create();

        // if patient doesn't have an appointment, then set an appointment
        if (newPatient.getAppointment() != null) {
            newPatient.getAppointment().setPatient(newPatient);
        }
        // get patients selected schedule YYYY-MM-DD
        LocalDate patientScheduleDate = newPatient.getAppointment().getScheduleDate();
        // convert the date into day ex. monday, tuesday...
        DayOfWeek patientSelectedDay = patientScheduleDate.getDayOfWeek();
        // convert the date into day ex. monday, tuesday... from enum
        AvailableDay doctorScheduleDay = AvailableDay.valueOf(patientSelectedDay.toString());
        // find doctors with the same schedule with patient
        List<Doctor> availableDoctors = doctorRepository.findDoctorsByAvailableDay(doctorScheduleDay);
        if(!availableDoctors.isEmpty()) {
            newPatient.getAppointment().setDoctor(availableDoctors.getFirst()); // save the first available doctor to the newly created patient
        }

        Patient addedPatient = patientRepository.save(newPatient); // save patient to the db

        return Optional.of(addedPatient);
    }

    private String getVerificationNumber() {
        // creates a random letter and number
        return UUID.randomUUID().toString();
    }

}
