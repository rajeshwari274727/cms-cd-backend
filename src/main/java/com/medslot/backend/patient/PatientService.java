package com.medslot.backend.patient;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return patientRepository.existsByEmail(email);
    }

    @Transactional
    public Patient registerPatient(Patient patient) {

        if (patientRepository.existsByEmail(patient.getEmail())) {
            throw new IllegalArgumentException(
                "Email is already registered"
            );
        }

        String hashedPassword =
            passwordEncoder.encode(patient.getPasswordHash());

        patient.setPasswordHash(hashedPassword);

        return patientRepository.save(patient);
    }

    @Transactional(readOnly = true)
    public Patient loginPatient(String email, String password) {

        Patient patient = patientRepository.findByEmail(email)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Invalid email or password"
                )
            );

        if (!passwordEncoder.matches(
                password,
                patient.getPasswordHash())) {

            throw new IllegalArgumentException(
                "Invalid email or password"
            );
        }

        return patient;
    }
}