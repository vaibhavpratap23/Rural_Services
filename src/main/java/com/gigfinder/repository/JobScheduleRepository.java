package com.gigfinder.repository;

import com.gigfinder.model.JobSchedule;
import com.gigfinder.model.WorkerProfile;
import com.gigfinder.model.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobScheduleRepository extends JpaRepository<JobSchedule, Long> {
    List<JobSchedule> findByWorker(WorkerProfile worker);
    List<JobSchedule> findByWorkerAndStatus(WorkerProfile worker, ScheduleStatus status);
    Optional<JobSchedule> findByJobId(Long jobId);
    List<JobSchedule> findByScheduledDateBetween(LocalDateTime start, LocalDateTime end);
    List<JobSchedule> findByWorkerAndScheduledDateBetween(WorkerProfile worker, LocalDateTime start, LocalDateTime end);
}
