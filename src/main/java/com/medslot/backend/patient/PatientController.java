package com.medslot.backend.patient;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmail(
            @RequestParam String email) {

        if (patientService.emailExists(email)) {
            return ResponseEntity.badRequest()
                    .body("Email is already registered");
        }

        return ResponseEntity.ok("Email is available");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(
            @RequestBody Patient patient) {

        try {
            Patient savedPatient =
                    patientService.registerPatient(patient);

            savedPatient.setPasswordHash(null);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedPatient);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginPatient(
            @RequestBody LoginRequest request) {

        try {
            Patient patient = patientService.loginPatient(
                    request.getEmail(),
                    request.getPassword()
            );

            patient.setPasswordHash(null);

            return ResponseEntity.ok(patient);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public LoginRequest() {
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}