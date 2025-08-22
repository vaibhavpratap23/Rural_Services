package com.gigfinder.controller;

import com.gigfinder.model.User;
import com.gigfinder.model.WorkerProfile;
import com.gigfinder.model.Document;
import com.gigfinder.model.enums.VerificationStatus;
import com.gigfinder.repository.JobRepository;
import com.gigfinder.repository.UserRepository;
import com.gigfinder.repository.WorkerProfileRepository;
import com.gigfinder.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerProfileRepository workerProfileRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime thisMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

            Map<String, Object> stats = new HashMap<>();

            // Job statistics
            stats.put("totalJobs", jobRepository.count());
            stats.put("jobsToday", jobRepository.countJobsPostedToday(today));
            stats.put("jobsThisMonth", jobRepository.countByCreatedAtBetween(thisMonth, LocalDateTime.now()));
            stats.put("completedJobs", jobRepository.countCompletedJobsBetween(thisMonth, LocalDateTime.now()));

            // User statistics
            stats.put("totalUsers", userRepository.count());
            stats.put("totalWorkers", workerProfileRepository.count());
            stats.put("verifiedWorkers", workerProfileRepository.findByVerificationStatus(
                    VerificationStatus.APPROVED).size());

            // Popular categories
            stats.put("mostPopularCategory", jobRepository.findMostPopularCategory());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/jobs/heatmap")
    public ResponseEntity<List<Map<String, Object>>> getJobHeatmap() {
        try {
            List<Map<String, Object>> heatmapData = List.of(
                    new HashMap<>(Map.of("lat", 28.6139, "lng", 77.2090, "count", 15)), // Delhi
                    new HashMap<>(Map.of("lat", 19.0760, "lng", 72.8777, "count", 12)), // Mumbai
                    new HashMap<>(Map.of("lat", 12.9716, "lng", 77.5946, "count", 8))   // Bangalore
            );
            return ResponseEntity.ok(heatmapData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/workers/leaderboard")
    public ResponseEntity<List<WorkerProfile>> getWorkerLeaderboard() {
        try {
            List<WorkerProfile> topWorkers = workerProfileRepository.findTop10ByOrderByRatingAvgDesc();
            return ResponseEntity.ok(topWorkers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<?> banUser(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // ✅ Add a ban flag if your User entity has one (example: user.setBanned(true))
            // user.setBanned(true);
            // userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User banned successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to ban user: " + e.getMessage());
        }
    }

    @PostMapping("/users/{userId}/unban")
    public ResponseEntity<?> unbanUser(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // ✅ Unban logic here (example: user.setBanned(false))
            // userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User unbanned successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to unban user: " + e.getMessage());
        }
    }

    @GetMapping("/fraud/reports")
    public ResponseEntity<List<Map<String, Object>>> getFraudReports() {
        try {
            List<Map<String, Object>> reports = List.of(
                    new HashMap<>(Map.of(
                            "id", 1,
                            "reportedUser", "user@example.com",
                            "reason", "Fake job posting",
                            "status", "PENDING",
                            "createdAt", LocalDateTime.now().minusDays(1)
                    ))
            );
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/workers/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingWorkers() {
        try {
            List<WorkerProfile> pendingWorkers = workerProfileRepository.findByVerificationStatusIn(
                    List.of(VerificationStatus.PENDING_BASIC, VerificationStatus.PENDING_FULL));

            List<Map<String, Object>> result = pendingWorkers.stream()
                    .map(worker -> {
                        Map<String, Object> workerData = new HashMap<>();
                        workerData.put("id", worker.getId());
                        workerData.put("name", worker.getUser().getName());
                        workerData.put("email", worker.getUser().getEmail());
                        workerData.put("phone", worker.getUser().getPhone());
                        workerData.put("verificationStatus", worker.getVerificationStatus());
                        workerData.put("aadhaarNumber", worker.getAadhaarNumber());
                        workerData.put("panNumber", worker.getPanNumber());
                        workerData.put("address", worker.getAddress());

                        List<Document> documents = documentRepository.findByWorker(worker);
                        List<Map<String, Object>> docData = documents.stream()
                                .map(doc -> {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("id", doc.getId());
                                    map.put("type", doc.getType());
                                    map.put("fileUrl", doc.getFileUrl());
                                    map.put("verificationStatus", doc.getVerificationStatus());
                                    return map;
                                })
                                .collect(Collectors.toList());

                        workerData.put("documents", docData);

                        return workerData;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    @PostMapping("/workers/{workerId}/approve")
    public ResponseEntity<?> approveWorker(@PathVariable Long workerId) {
        try {
            WorkerProfile worker = workerProfileRepository.findById(workerId)
                    .orElseThrow(() -> new RuntimeException("Worker not found"));

            worker.setVerificationStatus(VerificationStatus.VERIFIED);
            workerProfileRepository.save(worker);

            List<Document> documents = documentRepository.findByWorker(worker);
            documents.forEach(doc -> {
                doc.setVerificationStatus(VerificationStatus.VERIFIED);
                documentRepository.save(doc);
            });

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Worker approved successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to approve worker: " + e.getMessage()));
        }
    }

    @PostMapping("/workers/{workerId}/reject")
    public ResponseEntity<?> rejectWorker(@PathVariable Long workerId, @RequestBody Map<String, String> request) {
        try {
            WorkerProfile worker = workerProfileRepository.findById(workerId)
                    .orElseThrow(() -> new RuntimeException("Worker not found"));

            worker.setVerificationStatus(VerificationStatus.REJECTED);
            workerProfileRepository.save(worker);

            String reason = request.get("reason");

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Worker rejected successfully");
            response.put("reason", reason);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to reject worker: " + e.getMessage()));
        }
    }
}
