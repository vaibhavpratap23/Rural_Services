package com.gigfinder.controller;

import com.gigfinder.model.Job;
import com.gigfinder.model.Rating;
import com.gigfinder.model.User;
import com.gigfinder.model.enums.Role;
import com.gigfinder.repository.JobRepository;
import com.gigfinder.repository.RatingRepository;
import com.gigfinder.repository.UserRepository;
import com.gigfinder.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingRepository ratingRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/job/{jobId}")
    public ResponseEntity<?> rateJob(@PathVariable Long jobId, @RequestBody Map<String, Object> request) {
        try {
            User currentUser = authService.getCurrentUser();
            Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

            if (!"COMPLETED".equals(job.getStatus().name())) {
                return ResponseEntity.badRequest().body("Can only rate completed jobs");
            }

            // ✅ FIXED: use Role enum comparison and handle ClientProfile to User conversion
            User reviewee = currentUser.getRole() == Role.CLIENT
                    ? job.getWorker()
                    : job.getClient().getUser();

            if (reviewee == null) {
                return ResponseEntity.badRequest().body("Invalid job for rating");
            }

            // Prevent duplicate ratings
            if (ratingRepository.existsByJobAndReviewer(jobId, currentUser)) {
                return ResponseEntity.badRequest().body("You have already rated this job");
            }

            Integer score = (Integer) request.get("score");
            String comment = (String) request.get("comment");

            if (score == null || score < 1 || score > 5) {
                return ResponseEntity.badRequest().body("Score must be between 1 and 5");
            }

            Rating rating = Rating.builder()
                .job(job)
                .reviewer(currentUser)
                .reviewee(reviewee)
                .score(score)
                .comment(comment)
                .build();

            ratingRepository.save(rating);

            return ResponseEntity.ok(Map.of("message", "Rating submitted successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error submitting rating: " + e.getMessage());
        }
    }

    @GetMapping("/worker/{workerId}")
    public ResponseEntity<?> getWorkerRatings(@PathVariable Long workerId) {
        try {
            User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));

            List<Rating> ratings = ratingRepository.findByReviewee(worker);
            BigDecimal avgRating = ratingRepository.getAverageRatingByReviewee(worker);
            Long totalRatings = ratingRepository.getRatingCountByReviewee(worker);

            Map<String, Object> response = new HashMap<>();
            response.put("ratings", ratings);
            response.put("averageRating", avgRating != null ? avgRating.doubleValue() : 0.0);
            response.put("totalRatings", totalRatings);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching ratings: " + e.getMessage());
        }
    }

    @GetMapping("/job/{jobId}/can-rate")
    public ResponseEntity<?> canRateJob(@PathVariable Long jobId) {
        try {
            User currentUser = authService.getCurrentUser();
            Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

            boolean canRate = "COMPLETED".equals(job.getStatus().name()) &&
                             !ratingRepository.existsByJobAndReviewer(jobId, currentUser) &&
                             (job.getClient().equals(currentUser) || job.getWorker().equals(currentUser));

            return ResponseEntity.ok(Map.of("canRate", canRate));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error checking rating eligibility: " + e.getMessage());
        }
    }
}
