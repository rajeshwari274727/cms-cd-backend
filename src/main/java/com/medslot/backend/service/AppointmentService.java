package com.medslot.backend.service;

import com.medslot.backend.appointment.Appointment;
import com.medslot.backend.appointment.AppointmentRepository;
import com.medslot.backend.appointment.Doctor;
import com.medslot.backend.appointment.DoctorRepository;
import com.medslot.backend.patient.Patient;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
    }

    public Appointment bookAppointment(
            Patient patient,
            Long doctorId,
            LocalDate date,
            LocalTime time,
            String reason) {

        if (patient == null) {
            throw new IllegalArgumentException("Patient is required.");
        }

        if (doctorId == null) {
            throw new IllegalArgumentException("Doctor ID is required.");
        }

        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Choose today or a future appointment date.");
        }

        if (time == null) {
            throw new IllegalArgumentException("Appointment time is required.");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Doctor not found."));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);
        appointment.setReason(reason);
        appointment.setStatus("BOOKED");

        return appointmentRepository.save(appointment);
    }
}