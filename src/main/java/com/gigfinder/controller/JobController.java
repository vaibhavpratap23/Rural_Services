package com.gigfinder.controller;

import com.gigfinder.dto.*;
import com.gigfinder.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Slf4j
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobResponseDTO> createJob(@Valid @RequestBody JobRequestDTO request) {
        try {
            JobResponseDTO response = jobService.createJob(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating job", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<JobResponseDTO>> getAllOpenJobs() {
        return ResponseEntity.ok(jobService.getAllOpenJobs());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyJobs() {
        try {
            return ResponseEntity.ok(jobService.getMyJobs());
        } catch (Exception e) {
            log.error("Error fetching my jobs", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(jobService.getJobById(id));
        } catch (Exception e) {
            log.error("Job not found: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Job not found"));
        }
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptJob(@PathVariable Long id) {
        return handleJobAction(id, jobService::acceptJob, "accepting");
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<?> startJob(@PathVariable Long id) {
        return handleJobAction(id, jobService::startJob, "starting");
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeJob(@PathVariable Long id) {
        return handleJobAction(id, jobService::completeJob, "completing");
    }

    @PostMapping("/{id}/rating")
    public ResponseEntity<?> rateJob(@PathVariable Long id, @Valid @RequestBody RatingDTO ratingDTO) {
        try {
            String message = jobService.rateJob(id, ratingDTO);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            log.error("Error rating job {}", id, e);
            return buildErrorResponse(e);
        }
    }

    @PostMapping("/reports")
    public ResponseEntity<?> reportUser(@Valid @RequestBody ReportDTO reportDTO) {
        try {
            String message = jobService.reportUser(reportDTO);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            log.error("Error reporting user", e);
            return buildErrorResponse(e);
        }
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyJobs(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double radiusKm) {
        try {
            return ResponseEntity.ok(jobService.getNearbyJobs(latitude, longitude, radiusKm));
        } catch (Exception e) {
            log.error("Error fetching nearby jobs", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch nearby jobs"));
        }
    }

    @GetMapping("/worker/nearby")
    public ResponseEntity<?> getJobsForWorker() {
        try {
            return ResponseEntity.ok(jobService.getJobsForWorker());
        } catch (Exception e) {
            log.error("Error fetching jobs for worker", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getJobCategories() {
        try {
            return ResponseEntity.ok(jobService.getJobCategories());
        } catch (Exception e) {
            log.error("Error fetching job categories", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch categories"));
        }
    }

    @GetMapping("/ratings")
    public ResponseEntity<?> getRatingHistory() {
        try {
            return ResponseEntity.ok(jobService.getRatingHistory());
        } catch (Exception e) {
            log.error("Error fetching rating history", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch ratings"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchJobs(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(jobService.searchJobs(location, category, minPrice, maxPrice, status, page, size));
        } catch (Exception e) {
            log.error("Error searching jobs", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Error searching jobs: " + e.getMessage()));
        }
    }

    // -------------------------
    // 🔧 Utility methods
    // -------------------------

    private ResponseEntity<?> handleJobAction(Long jobId,
                                              JobAction action,
                                              String actionName) {
        try {
            return ResponseEntity.ok(action.execute(jobId));
        } catch (Exception e) {
            log.error("Error {} job {}", actionName, jobId, e);
            return buildErrorResponse(e);
        }
    }

    private ResponseEntity<?> buildErrorResponse(Exception e) {
        String msg = e.getMessage() != null ? e.getMessage() : "Unknown error";
        if (msg.contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", msg));
        } else if (msg.contains("Not authorized")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", msg));
        } else if (msg.contains("cannot")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", msg));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", msg));
        }
    }

    @FunctionalInterface
    private interface JobAction {
        JobResponseDTO execute(Long jobId) throws Exception;
    }
}
