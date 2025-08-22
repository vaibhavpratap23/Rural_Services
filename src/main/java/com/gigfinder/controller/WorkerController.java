package com.gigfinder.controller;

import com.gigfinder.model.User;
import com.gigfinder.model.WorkerProfile;
import com.gigfinder.repository.UserRepository;
import com.gigfinder.repository.WorkerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    @Autowired
    private WorkerProfileRepository workerProfileRepository;

    @Autowired
    private UserRepository userRepository;

    // Register or update worker profile for authenticated user
    @PostMapping
    public ResponseEntity<?> registerOrUpdateWorker(@RequestBody WorkerProfile profile) {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }
            User user = userOpt.get();
            profile.setUser(user);

            if (profile.getRadiusKm() == null) {
                profile.setRadiusKm(5);
            }
            if (profile.getVerificationStatus() == null) {
                profile.setVerificationStatus(com.gigfinder.model.enums.VerificationStatus.PENDING);
            }

            WorkerProfile savedProfile = workerProfileRepository.save(profile);
            return ResponseEntity.ok(savedProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to save worker profile: " + e.getMessage());
        }
    }

    // List all workers
    @GetMapping
    public List<WorkerProfile> getAllWorkers() {
        return workerProfileRepository.findAll();
    }

    // Search workers by skill
    @GetMapping("/search")
    public List<WorkerProfile> searchWorkersBySkill(@RequestParam String skill) {
        return workerProfileRepository.findBySkillsContaining(skill);
    }

    // Get current worker's profile
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }
            User user = userOpt.get();
            Optional<WorkerProfile> profileOpt = workerProfileRepository.findByUser(user);
            if (profileOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(profileOpt.get());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch profile: " + e.getMessage());
        }
    }

    // Update verification documents
    @PutMapping("/verification")
    public ResponseEntity<?> updateVerification(@RequestBody Map<String, String> documents) {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }
            User user = userOpt.get();
            Optional<WorkerProfile> profileOpt = workerProfileRepository.findByUser(user);
            if (profileOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            WorkerProfile profile = profileOpt.get();
            // Update verification status based on documents provided
            if (documents.containsKey("aadhaar") && documents.containsKey("addressProof")) {
                profile.setVerificationStatus(com.gigfinder.model.enums.VerificationStatus.PENDING);
            }
            
            WorkerProfile savedProfile = workerProfileRepository.save(profile);
            return ResponseEntity.ok(savedProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update verification: " + e.getMessage());
        }
    }

    // Toggle worker availability
    @PutMapping("/availability")
    public ResponseEntity<?> toggleAvailability(@RequestBody Map<String, Boolean> request) {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }
            User user = userOpt.get();
            Optional<WorkerProfile> profileOpt = workerProfileRepository.findByUser(user);
            if (profileOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            WorkerProfile profile = profileOpt.get();
            Boolean isAvailable = request.get("isAvailable");
            if (isAvailable != null) {
                profile.setIsAvailable(isAvailable);
                WorkerProfile savedProfile = workerProfileRepository.save(profile);
                return ResponseEntity.ok(savedProfile);
            }
            return ResponseEntity.badRequest().body("isAvailable field is required");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update availability: " + e.getMessage());
        }
    }

    // Get available workers for instant hire
    @GetMapping("/available")
    public ResponseEntity<List<WorkerProfile>> getAvailableWorkers(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "10.0") Double radiusKm) {
        try {
            List<WorkerProfile> workers = workerProfileRepository.findAvailableWorkers(category, latitude, longitude, radiusKm);
            return ResponseEntity.ok(workers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
