package com.bibliotheque.universitaire.serviceemprunts.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job penaliteJob;

    @GetMapping("/api/batch/run")
    public ResponseEntity<String> runBatch() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()) // Ajoute un paramètre unique pour éviter les conflits
                    .toJobParameters();
            jobLauncher.run(penaliteJob, jobParameters);
            return ResponseEntity.ok("Batch started successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to start batch: " + e.getMessage());
        }
    }
}
