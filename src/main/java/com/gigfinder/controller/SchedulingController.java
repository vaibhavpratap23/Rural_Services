package com.gigfinder.controller;

import com.gigfinder.dto.JobScheduleDTO;
import com.gigfinder.dto.WorkerAvailabilityDTO;
import com.gigfinder.model.JobSchedule;
import com.gigfinder.model.WorkerAvailability;
import com.gigfinder.model.enums.ScheduleStatus;
import com.gigfinder.service.SchedulingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/scheduling")
@RequiredArgsConstructor
public class SchedulingController {
    
    private final SchedulingService schedulingService;
    
    @PostMapping("/availability")
    public ResponseEntity<?> setWorkerAvailability(@Valid @RequestBody List<WorkerAvailabilityDTO> availabilities) {
        try {
            List<WorkerAvailability> savedAvailabilities = schedulingService.setWorkerAvailability(
                availabilities.stream()
                    .map(this::convertToEntity)
                    .collect(Collectors.toList())
            );
            return ResponseEntity.ok(savedAvailabilities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error setting availability: " + e.getMessage());
        }
    }
    
    @GetMapping("/availability/{workerId}")
    public ResponseEntity<?> getWorkerAvailability(@PathVariable Long workerId) {
        try {
            List<WorkerAvailability> availabilities = schedulingService.getWorkerAvailability(workerId);
            return ResponseEntity.ok(availabilities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching availability: " + e.getMessage());
        }
    }
    
    @PostMapping("/jobs/{jobId}/schedule")
    public ResponseEntity<?> scheduleJob(
            @PathVariable Long jobId,
            @RequestParam LocalDateTime scheduledDate,
            @RequestParam Integer estimatedDuration) {
        try {
            JobSchedule schedule = schedulingService.scheduleJob(jobId, scheduledDate, estimatedDuration);
            return ResponseEntity.ok(convertToScheduleDTO(schedule));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error scheduling job: " + e.getMessage());
        }
    }
    
    @PutMapping("/schedules/{scheduleId}/status")
    public ResponseEntity<?> updateScheduleStatus(
            @PathVariable Long scheduleId,
            @RequestParam ScheduleStatus status) {
        try {
            JobSchedule schedule = schedulingService.updateScheduleStatus(scheduleId, status);
            return ResponseEntity.ok(convertToScheduleDTO(schedule));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating schedule: " + e.getMessage());
        }
    }
    
    @GetMapping("/worker/{workerId}/schedules")
    public ResponseEntity<?> getWorkerSchedules(@PathVariable Long workerId) {
        try {
            List<JobSchedule> schedules = schedulingService.getWorkerSchedules(workerId);
            return ResponseEntity.ok(schedules.stream()
                .map(this::convertToScheduleDTO)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching worker schedules: " + e.getMessage());
        }
    }
    
    @GetMapping("/client/schedules")
    public ResponseEntity<?> getClientSchedules() {
        try {
            List<JobSchedule> schedules = schedulingService.getClientSchedules();
            return ResponseEntity.ok(schedules.stream()
                .map(this::convertToScheduleDTO)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching client schedules: " + e.getMessage());
        }
    }
    
    private WorkerAvailability convertToEntity(WorkerAvailabilityDTO dto) {
        return WorkerAvailability.builder()
                .dayOfWeek(dto.getDayOfWeek())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .isAvailable(dto.getIsAvailable())
                .build();
    }
    
    private WorkerAvailabilityDTO convertToDTO(WorkerAvailability availability) {
        WorkerAvailabilityDTO dto = new WorkerAvailabilityDTO();
        dto.setId(availability.getId());
        dto.setDayOfWeek(availability.getDayOfWeek());
        dto.setStartTime(availability.getStartTime());
        dto.setEndTime(availability.getEndTime());
        dto.setIsAvailable(availability.getIsAvailable());
        return dto;
    }
    
    private JobScheduleDTO convertToScheduleDTO(JobSchedule schedule) {
        JobScheduleDTO dto = new JobScheduleDTO();
        dto.setId(schedule.getId());
        dto.setJobId(schedule.getJob().getId());
        dto.setJobTitle(schedule.getJob().getTitle());
        dto.setWorkerId(schedule.getWorker().getId());
        dto.setWorkerName(schedule.getWorker().getUser().getName());
        dto.setClientName(schedule.getJob().getClient().getUser().getName());
        dto.setScheduledDate(schedule.getScheduledDate());
        dto.setEstimatedDurationHours(schedule.getEstimatedDurationHours());
        dto.setStatus(schedule.getStatus());
        dto.setNotes(schedule.getNotes());
        dto.setCreatedAt(schedule.getCreatedAt());
        return dto;
    }
}
