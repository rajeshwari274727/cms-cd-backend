package com.medslot.backend.appointment;

import com.medslot.backend.patient.Patient;
import com.medslot.backend.patient.PatientRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    public AppointmentController(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository) {

        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
    }

    // Get all doctors
    @GetMapping("/doctors")
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // Get all appointments
    @GetMapping("/appointments")
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // Create an appointment
    @PostMapping("/appointments")
    public ResponseEntity<?> createAppointment(
            @RequestBody Map<String, Object> request) {

        try {
            Object patientIdValue = request.get("patientId");
            Object doctorIdValue = request.get("doctorId");

            if (patientIdValue == null || doctorIdValue == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("message", "Patient ID and Doctor ID are required")
                );
            }

            Long patientId = Long.valueOf(patientIdValue.toString());
            Long doctorId = Long.valueOf(doctorIdValue.toString());

            String dateValue = String.valueOf(
                request.get("appointmentDate")
            );

            String timeValue = String.valueOf(
                request.get("appointmentTime")
            );

            String reason = String.valueOf(request.get("reason"));

            if (dateValue.isBlank() || timeValue.isBlank()
                    || reason.isBlank()
                    || "null".equals(dateValue)
                    || "null".equals(timeValue)
                    || "null".equals(reason)) {

                return ResponseEntity.badRequest().body(
                    Map.of("message",
                        "Appointment date, time, and reason are required")
                );
            }

            Optional<Patient> patientOptional =
                patientRepository.findById(patientId);

            if (patientOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("message", "Patient not found")
                );
            }

            Optional<Doctor> doctorOptional =
                doctorRepository.findById(doctorId);

            if (doctorOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("message", "Doctor not found")
                );
            }

            Appointment appointment = new Appointment();

            appointment.setPatient(patientOptional.get());
            appointment.setDoctor(doctorOptional.get());
            appointment.setAppointmentDate(
                LocalDate.parse(dateValue)
            );
            appointment.setAppointmentTime(
                LocalTime.parse(timeValue)
            );
            appointment.setReason(reason);
            appointment.setStatus("PENDING");

            Appointment saved =
                appointmentRepository.save(appointment);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                    "message", "Appointment booked successfully",
                    "appointmentId", saved.getAppointmentId(),
                    "status", saved.getStatus()
                )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                Map.of("message",
                    "Invalid appointment date, time, patient ID, or doctor ID")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "message", "Unable to book appointment. Check backend logs."
                ));
        }
    }

    // Approve or reject an appointment
    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String status = request.get("status");

        if (status == null ||
                !(status.equalsIgnoreCase("APPROVED")
                || status.equalsIgnoreCase("REJECTED"))) {

            return ResponseEntity.badRequest().body(
                Map.of("message",
                    "Status must be APPROVED or REJECTED")
            );
        }

        return appointmentRepository.findById(id)
            .map(appointment -> {
                appointment.setStatus(status.toUpperCase());

                Appointment updated =
                    appointmentRepository.save(appointment);

                return ResponseEntity.ok(Map.of(
                    "message", "Appointment status updated",
                    "appointmentId", updated.getAppointmentId(),
                    "status", updated.getStatus()
                ));
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Appointment not found")));
    }
}