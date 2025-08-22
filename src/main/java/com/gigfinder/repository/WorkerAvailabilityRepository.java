package com.gigfinder.repository;

import com.gigfinder.model.WorkerAvailability;
import com.gigfinder.model.WorkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface WorkerAvailabilityRepository extends JpaRepository<WorkerAvailability, Long> {
    List<WorkerAvailability> findByWorker(WorkerProfile worker);
    List<WorkerAvailability> findByWorkerAndDayOfWeek(WorkerProfile worker, DayOfWeek dayOfWeek);
    List<WorkerAvailability> findByWorkerAndIsAvailableTrue(WorkerProfile worker);
    void deleteByWorker(WorkerProfile worker);
}
