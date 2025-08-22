package com.gigfinder.repository;

import com.gigfinder.model.JobAssignment;
import com.gigfinder.model.Job;
import com.gigfinder.model.WorkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobAssignmentRepository extends JpaRepository<JobAssignment, Long> {
    
    Optional<JobAssignment> findByJob(Job job);
    
    List<JobAssignment> findByWorker(WorkerProfile worker);
    
    List<JobAssignment> findByWorkerAndStatus(WorkerProfile worker, com.gigfinder.model.enums.AssignmentStatus status);
    
    boolean existsByJob(Job job);
}







