package com.gigfinder.repository;

import com.gigfinder.model.Document;
import com.gigfinder.model.WorkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByWorker(WorkerProfile worker);
    List<Document> findByWorkerOrderByCreatedAtDesc(WorkerProfile worker);
}
