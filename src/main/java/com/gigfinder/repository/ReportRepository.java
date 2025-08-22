package com.gigfinder.repository;

import com.gigfinder.model.Report;
import com.gigfinder.model.User;
import com.gigfinder.model.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByReporter(User reporter);
    List<Report> findByReported(User reported);
    List<Report> findByStatus(ReportStatus status);
    List<Report> findByReporterAndReported(User reporter, User reported);
}






