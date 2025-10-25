package com.example.demo.repository;

import com.example.demo.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByReportTypeOrderByGeneratedDateDesc(Report.ReportType reportType);
    List<Report> findByReportTypeAndPeriodOrderByGeneratedDateDesc(Report.ReportType reportType, Report.ReportPeriod period);
}
