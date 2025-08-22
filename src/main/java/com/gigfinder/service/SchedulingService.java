package com.gigfinder.service;

import com.gigfinder.model.*;
import com.gigfinder.model.enums.ScheduleStatus;
import com.gigfinder.repository.*;
import com.gigfinder.model.JobAssignment;
import com.gigfinder.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchedulingService {
    
    private final WorkerAvailabilityRepository availabilityRepository;
    private final JobScheduleRepository scheduleRepository;
    private final JobRepository jobRepository;
    private final JobAssignmentRepository jobAssignmentRepository;
    private final WorkerProfileRepository workerRepository;
    private final UserRepository userRepository;
    
    public List<WorkerAvailability> setWorkerAvailability(List<WorkerAvailability> availabilities) {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        WorkerProfile worker = workerRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Worker profile not found"));
        
        // Clear existing availability for this worker
        availabilityRepository.deleteByWorker(worker);
        
        // Set new availability
        availabilities.forEach(availability -> availability.setWorker(worker));
        return availabilityRepository.saveAll(availabilities);
    }
    
    public List<WorkerAvailability> getWorkerAvailability(Long workerId) {
        WorkerProfile worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        return availabilityRepository.findByWorker(worker);
    }
    
    public JobSchedule scheduleJob(Long jobId, LocalDateTime scheduledDate, Integer estimatedDuration) {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        
        // Verify the current user is the client of this job
        if (!job.getClient().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to schedule this job");
        }
        
        // Check if job is already scheduled
        Optional<JobSchedule> existingSchedule = scheduleRepository.findByJobId(jobId);
        if (existingSchedule.isPresent()) {
            throw new RuntimeException("Job is already scheduled");
        }
        
        // Get the assigned worker from JobAssignment
        var assignment = jobAssignmentRepository.findByJob(job)
                .orElseThrow(() -> new RuntimeException("No worker assigned to this job"));
        
        // Check worker availability for the scheduled date
        if (!isWorkerAvailable(assignment.getWorker(), scheduledDate, estimatedDuration)) {
            throw new RuntimeException("Worker is not available at the scheduled time");
        }
        
        JobSchedule schedule = JobSchedule.builder()
                .job(job)
                .worker(assignment.getWorker())
                .scheduledDate(scheduledDate)
                .estimatedDurationHours(estimatedDuration)
                .status(ScheduleStatus.SCHEDULED)
                .build();
        
        return scheduleRepository.save(schedule);
    }
    
    public JobSchedule updateScheduleStatus(Long scheduleId, ScheduleStatus status) {
        JobSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        
        schedule.setStatus(status);
        return scheduleRepository.save(schedule);
    }
    
    public List<JobSchedule> getWorkerSchedules(Long workerId) {
        WorkerProfile worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        return scheduleRepository.findByWorker(worker);
    }
    
    public List<JobSchedule> getClientSchedules() {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get all jobs for this client and their schedules
        List<Job> clientJobs = jobRepository.findByClientUser(currentUser);
        return clientJobs.stream()
                .map(job -> scheduleRepository.findByJobId(job.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
    
    private boolean isWorkerAvailable(WorkerProfile worker, LocalDateTime scheduledDate, Integer durationHours) {
        DayOfWeek dayOfWeek = scheduledDate.getDayOfWeek();
        LocalTime startTime = scheduledDate.toLocalTime();
        LocalTime endTime = startTime.plusHours(durationHours);
        
        List<WorkerAvailability> dayAvailability = availabilityRepository
                .findByWorkerAndDayOfWeek(worker, dayOfWeek);
        
        return dayAvailability.stream()
                .anyMatch(availability -> 
                    availability.getIsAvailable() &&
                    availability.getStartTime().isBefore(startTime) &&
                    availability.getEndTime().isAfter(endTime));
    }
}
