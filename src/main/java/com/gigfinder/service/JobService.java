package com.gigfinder.service;

import com.gigfinder.dto.JobRequestDTO;
import com.gigfinder.dto.JobResponseDTO;
import com.gigfinder.dto.RatingDTO;
import com.gigfinder.dto.RatingResponseDTO;
import com.gigfinder.dto.ReportDTO;
import com.gigfinder.model.*;
import com.gigfinder.model.enums.JobStatus;
import com.gigfinder.model.enums.AssignmentStatus;
import com.gigfinder.repository.*;
import com.gigfinder.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.persistence.criteria.Join;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final JobAssignmentRepository jobAssignmentRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final RatingRepository ratingRepository;
    private final ReportRepository reportRepository;
    private final NotificationRepository notificationRepository;

    public JobResponseDTO createJob(JobRequestDTO request) {

        // 1️⃣ Get current user
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("Unauthorized");
        }

        // 2️⃣ Get client profile
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ClientProfile clientProfile = clientProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        // 3️⃣ Get category and subcategory
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        }

        SubCategory subCategory = null;
        if (request.getSubCategoryId() != null) {
            subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new RuntimeException("SubCategory not found"));
        }

        // 4️⃣ Build and save job
        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(category)
                .subCategory(subCategory)
                .budget(request.getBudget())
                .address(request.getAddress())
                .status(JobStatus.OPEN)
                .client(clientProfile)
                .build();

        Job savedJob = jobRepository.save(job);
        
        // Notify nearby workers about new job
        List<WorkerProfile> nearbyWorkers = getNearbyWorkers(clientProfile.getLocationLat(), clientProfile.getLocationLng());
        for (WorkerProfile worker : nearbyWorkers) {
            notificationService.sendNewJobAvailable(worker.getUser().getId(), savedJob.getId(), savedJob.getTitle());
        }
        
        return convertToResponseDTO(savedJob);
    }

    public List<JobResponseDTO> getAllOpenJobs() {
        return jobRepository.findByStatusOrderByCreatedAtDesc(JobStatus.OPEN)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public JobResponseDTO getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return convertToResponseDTO(job);
    }

    public List<JobResponseDTO> getMyJobs() {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("Unauthorized");
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jobRepository.findByClientUser(user)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public JobResponseDTO acceptJob(Long jobId) {
        // 1️⃣ Get the username (email) of the logged in user
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("Unauthorized");
        }

        // 2️⃣ Find the User and WorkerProfile for this user
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WorkerProfile workerProfile = workerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Worker profile not found"));

        // 3️⃣ Find the job and validate it can be accepted
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getStatus() != JobStatus.OPEN) {
            throw new RuntimeException("Job is not available for acceptance");
        }

        if (jobAssignmentRepository.existsByJob(job)) {
            throw new RuntimeException("Job is already assigned");
        }

        // 4️⃣ Create job assignment
        JobAssignment assignment = JobAssignment.builder()
                .job(job)
                .worker(workerProfile)
                .status(AssignmentStatus.ASSIGNED)
                .assignedAt(LocalDateTime.now())
                .build();

        jobAssignmentRepository.save(assignment);

        // 5️⃣ Create notification for worker
        Notification workerNotification = Notification.builder()
                .user(workerProfile.getUser())
                .title("Job Assigned")
                .message("You have been assigned to: " + job.getTitle())
                .readStatus(false)
                .createdAt(LocalDateTime.now())
                .build();

        // Save notification for worker
        notificationRepository.save(workerNotification);

        // 6️⃣ Create notification for client
        Notification clientNotification = Notification.builder()
                .user(job.getClient().getUser())
                .title("Job Accepted")
                .message("Your job '" + job.getTitle() + "' has been accepted by " + workerProfile.getUser().getName())
                .readStatus(false)
                .createdAt(LocalDateTime.now())
                .build();

        // Save notification for client
        notificationRepository.save(clientNotification);

        job.setStatus(JobStatus.ASSIGNED);
        job.setWorker(workerProfile.getUser());
        job.setAcceptedAt(LocalDateTime.now());
        jobRepository.save(job);
        
        // Send real-time notification to client
        notificationService.sendJobAccepted(job.getClient().getUser().getId(), jobId, workerProfile.getUser().getName());
        
        return convertToResponseDTO(job);
    }
    
    public JobResponseDTO completeJob(Long jobId) {
        // 1️⃣ Get the username (email) of the logged in user
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("Unauthorized");
        }

        // 2️⃣ Find the User and WorkerProfile for this user
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WorkerProfile workerProfile = workerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Worker profile not found"));

        // 3️⃣ Find the job and validate it can be completed
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getStatus() != JobStatus.ASSIGNED) {
            throw new RuntimeException("Job cannot be completed - not in ASSIGNED status");
        }

        // 4️⃣ Check if this worker is actually assigned to this job
        JobAssignment assignment = jobAssignmentRepository.findByJob(job)
                .orElseThrow(() -> new RuntimeException("Job assignment not found"));

        if (!assignment.getWorker().getId().equals(workerProfile.getId())) {
            throw new RuntimeException("Not authorized to complete this job");
        }

        // 5️⃣ Update job assignment status
        assignment.setStatus(AssignmentStatus.COMPLETED);
        assignment.setCompletedAt(LocalDateTime.now());
        jobAssignmentRepository.save(assignment);

        // 6️⃣ Create notification for client
        Notification clientNotification = Notification.builder()
                .user(job.getClient().getUser())
                .title("Job Completed")
                .message("Your job '" + job.getTitle() + "' has been completed by " + workerProfile.getUser().getName())
                .readStatus(false)
                .createdAt(LocalDateTime.now())
                .build();

        // Save notification for client
        notificationRepository.save(clientNotification);

        // 7️⃣ Update job status
        job.setStatus(JobStatus.COMPLETED);
        jobRepository.save(job);

        // Send real-time notification to client
        notificationService.sendJobCompleted(job.getClient().getUser().getId(), jobId);

        return convertToResponseDTO(job);
    }

    public JobResponseDTO startJob(Long jobId) {
        // 1️⃣ Get the username (email) of the logged in user
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("Unauthorized");
        }

        // 2️⃣ Find the User and WorkerProfile for this user
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WorkerProfile workerProfile = workerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Worker profile not found"));

        // 3️⃣ Find the job and validate it can be started
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getStatus() != JobStatus.ASSIGNED) {
            throw new RuntimeException("Job cannot be started - not in ASSIGNED status");
        }

        // 4️⃣ Check if this worker is actually assigned to this job
        JobAssignment assignment = jobAssignmentRepository.findByJob(job)
                .orElseThrow(() -> new RuntimeException("Job assignment not found"));

        if (!assignment.getWorker().getId().equals(workerProfile.getId())) {
            throw new RuntimeException("Not authorized to start this job");
        }

        // 5️⃣ Update job assignment status
        assignment.setStatus(AssignmentStatus.IN_PROGRESS);
        assignment.setStartedAt(LocalDateTime.now());
        jobAssignmentRepository.save(assignment);

        // 6️⃣ Create notification for client
        Notification clientNotification = Notification.builder()
                .user(job.getClient().getUser())
                .title("Job Started")
                .message("Your job '" + job.getTitle() + "' has been started by " + workerProfile.getUser().getName())
                .readStatus(false)
                .createdAt(LocalDateTime.now())
                .build();

        // Save notification for client
        notificationRepository.save(clientNotification);

        // 7️⃣ Update job status
        job.setStatus(JobStatus.IN_PROGRESS);
        jobRepository.save(job);

        // Send real-time notification
        notificationService.sendJobStarted(job.getClient().getUser().getId(), jobId);

        return convertToResponseDTO(job);
    }

    public List<JobResponseDTO> getNearbyJobs(Double latitude, Double longitude, Double radiusKm) {
        // Calculate bounding box for radius search
        double latDelta = radiusKm / 111.0; // 1 degree latitude ≈ 111 km
        double lonDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)));
        
        double minLat = latitude - latDelta;
        double maxLat = latitude + latDelta;
        double minLon = longitude - lonDelta;
        double maxLon = longitude + lonDelta;
        
        List<Job> nearbyJobs = jobRepository.findByLocationWithinBounds(
            minLat, maxLat, minLon, maxLon, JobStatus.OPEN);
        
        return nearbyJobs.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public String rateJob(Long jobId, RatingDTO ratingDTO) {
        // 1️⃣ Get the username (email) of the logged in user
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("Unauthorized");
        }

        // 2️⃣ Find the User and ClientProfile for this user
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ClientProfile clientProfile = clientProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        // 3️⃣ Find the job and validate it can be rated
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getStatus() != JobStatus.COMPLETED) {
            throw new RuntimeException("Job cannot be rated - not completed yet");
        }

        if (!job.getClient().getId().equals(clientProfile.getId())) {
            throw new RuntimeException("Not authorized to rate this job");
        }

        // 4️⃣ Get the worker who completed the job
        JobAssignment assignment = jobAssignmentRepository.findByJob(job)
                .orElseThrow(() -> new RuntimeException("Job assignment not found"));

        WorkerProfile worker = assignment.getWorker();

        // 5️⃣ Create and save the rating
        Rating rating = Rating.builder()
                .job(job)
                .reviewer(user)  // The client who is rating
                .reviewee(worker.getUser())  // The worker being rated
                .score(ratingDTO.getRating())
                .comment(ratingDTO.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        // Save rating
        ratingRepository.save(rating);

        // 6️⃣ Update worker's average rating
        BigDecimal newAverage = calculateNewAverage(worker, ratingDTO.getRating());
        worker.setRatingAvg(newAverage);
        workerProfileRepository.save(worker);

        return "Rating submitted successfully";
    }
    
    public String reportUser(ReportDTO reportDTO) {
        // 1️⃣ Get the username (email) of the logged in user
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("Unauthorized");
        }

        // 2️⃣ Find the User making the report
        User reporter = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3️⃣ Find the User being reported
        User reportedUser = userRepository.findById(reportDTO.getReportedUserId())
                .orElseThrow(() -> new RuntimeException("Reported user not found"));

        // 4️⃣ Prevent self-reporting
        if (reporter.getId().equals(reportedUser.getId())) {
            throw new RuntimeException("Cannot report yourself");
        }

        // 5️⃣ Create and save the report
        Report report = Report.builder()
                .reporter(reporter)
                .reported(reportedUser)
                .reason(reportDTO.getReason())
                .status(com.gigfinder.model.enums.ReportStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();

        // Save report
        reportRepository.save(report);

        return "Report submitted successfully";
    }
    
    public List<RatingResponseDTO> getRatingHistory() {
        try {
            String username = SecurityUtil.getCurrentUsername();
            if (username == null) {
                throw new RuntimeException("Unauthorized");
            }

            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Rating> ratings = ratingRepository.findByReviewee(user);
            
            return ratings.stream()
                    .map(this::convertToRatingResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching rating history: " + e.getMessage());
        }
    }

    public List<JobResponseDTO> searchJobs(String location, String category, Double minPrice, Double maxPrice, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Specification<Job> spec = Specification.where(null);
        
        if (location != null && !location.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("address")), "%" + location.toLowerCase() + "%"));
        }
        
        if (category != null && !category.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Job, Category> categoryJoin = root.join("category");
                return cb.equal(categoryJoin.get("name"), category);
            });
        }
        
        if (minPrice != null) {
            spec = spec.and((root, query, cb) -> 
                cb.greaterThanOrEqualTo(root.get("budget"), minPrice));
        }
        
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) -> 
                cb.lessThanOrEqualTo(root.get("budget"), maxPrice));
        }
        
        if (status != null && !status.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("status"), JobStatus.valueOf(status.toUpperCase())));
        }
        
        Page<Job> jobPage = jobRepository.findAll(spec, pageable);
        return jobPage.getContent().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<JobResponseDTO> getJobsForWorker() {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new RuntimeException("Unauthorized");
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WorkerProfile workerProfile = workerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Worker profile not found"));

        if (workerProfile.getLocationLat() == null || workerProfile.getLocationLng() == null) {
            return getAllOpenJobs(); // Return all jobs if no location set
        }

        // Get jobs within worker's service radius
        double latitude = workerProfile.getLocationLat().doubleValue();
        double longitude = workerProfile.getLocationLng().doubleValue();
        double radiusKm = workerProfile.getRadiusKm().doubleValue();

        return getNearbyJobs(latitude, longitude, radiusKm);
    }

    public Map<String, List<String>> getJobCategories() {
        Map<String, List<String>> categories = new HashMap<>();
        
        // Maids / Helpers
        categories.put("Maids / Helpers", Arrays.asList(
            "Full-time maid (all-rounder)", "Cleaning maid", "Cooking maid (home cook)",
            "Babysitter / Nanny", "Elderly caretaker", "Pet caretaker / Dog walker",
            "Deep Cleaning Specialists (sofa, carpet, kitchen, bathroom)", "Laundry & Ironing Services"
        ));
        
        // Food & Kitchen Services
        categories.put("Food & Kitchen Services", Arrays.asList(
            "Home Cooks / Chefs (daily meals or special occasions)", "Tiffin Services (homemade food delivery)",
            "Event Catering Helpers"
        ));
        
        // Education & Tutoring
        categories.put("Education & Tutoring", Arrays.asList(
            "Tuition Teachers (school/college subjects)", "Music Teachers (guitar, piano, singing, tabla etc.)",
            "Dance / Fitness Trainers (Zumba, Yoga, Gym trainer)", "Coding / Skill Tutors (Java, Python, Photoshop etc.)"
        ));
        
        // Appliance & Tech Repairs
        categories.put("Appliance & Tech Repairs", Arrays.asList(
            "Fridge, Washing Machine, TV, Geyser repairs", "Mobile / Laptop / Tablet repairs",
            "CCTV & Smart Home Installation"
        ));
        
        // Lifestyle & Personal Care
        categories.put("Lifestyle & Personal Care", Arrays.asList(
            "Beauticians (home salon)", "Massage therapists", "Personal trainers",
            "Dieticians / Nutritionists (consult online + offline)"
        ));
        
        // Vehicle & Travel Support
        categories.put("Vehicle & Travel Support", Arrays.asList(
            "Car cleaning & detailing", "Driver on demand", "Bike/Car mechanics (home visit)"
        ));
        
        // Event & Occasion Services
        categories.put("Event & Occasion Services", Arrays.asList(
            "Photographers & Videographers", "Decorators (birthday, small events)",
            "Pooja / Ritual helpers (pandit, arrangements)", "Party helpers (servers, decorators, sound system guys)"
        ));
        
        // Traditional Services (keeping existing ones)
        categories.put("Home Repairs", Arrays.asList(
            "Electrical", "Plumbing", "Construction", "Cleaning", "Painting", "AC Repair"
        ));
        
        return categories;
    }
    
    private List<WorkerProfile> getNearbyWorkers(BigDecimal clientLat, BigDecimal clientLng) {
        if (clientLat == null || clientLng == null) {
            return List.of(); // Return empty list if client location not set
        }
        
        double latitude = clientLat.doubleValue();
        double longitude = clientLng.doubleValue();
        double searchRadius = 10.0; // 10 km radius
        
        // Calculate bounding box for radius search
        double latDelta = searchRadius / 111.0; // 1 degree latitude ≈ 111 km
        double lonDelta = searchRadius / (111.0 * Math.cos(Math.toRadians(latitude)));
        
        double minLat = latitude - latDelta;
        double maxLat = latitude + latDelta;
        double minLon = longitude - lonDelta;
        double maxLon = longitude + lonDelta;
        
        return workerProfileRepository.findByLocationWithinBounds(
            BigDecimal.valueOf(minLat), 
            BigDecimal.valueOf(maxLat), 
            BigDecimal.valueOf(minLon), 
            BigDecimal.valueOf(maxLon)
        );
    }
    
    private BigDecimal calculateNewAverage(WorkerProfile worker, Integer newRating) {
        // Get current average and count
        BigDecimal currentAvg = ratingRepository.getAverageRatingByReviewee(worker.getUser());
        Long currentCount = ratingRepository.getRatingCountByReviewee(worker.getUser());
        
        if (currentAvg == null) {
            // First rating
            return BigDecimal.valueOf(newRating);
        }
        
        // Calculate new average: (current_avg * current_count + new_rating) / (current_count + 1)
        BigDecimal totalSum = currentAvg.multiply(BigDecimal.valueOf(currentCount))
                .add(BigDecimal.valueOf(newRating));
        BigDecimal newCount = BigDecimal.valueOf(currentCount + 1);
        
        return totalSum.divide(newCount, 2, BigDecimal.ROUND_HALF_UP);
    }
    
    private RatingResponseDTO convertToRatingResponseDTO(Rating rating) {
        return RatingResponseDTO.builder()
                .id(rating.getId())
                .jobTitle(rating.getJob().getTitle())
                .reviewerName(rating.getReviewer().getName())
                .revieweeName(rating.getReviewee().getName())
                .score(rating.getScore())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }

    private JobResponseDTO convertToResponseDTO(Job job) {
        return JobResponseDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .categoryName(job.getCategory().getName())
                .subCategoryName(job.getSubCategory() != null ? job.getSubCategory().getName() : null)
                .budget(job.getBudget())
                .address(job.getAddress())
                .status(job.getStatus().toString())
                .createdAt(job.getCreatedAt())
                .scheduledAt(job.getScheduledAt())
                .build();
    }
}
