package com.example.demo.controller;

import com.example.demo.domain.Report;
import com.example.demo.service.ReportService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Admin Reports - All report types
    @GetMapping("/admin")
    public String adminReports(Model model) {
        model.addAttribute("inventoryReports", reportService.getReportsByType(Report.ReportType.INVENTORY));
        model.addAttribute("salesReports", reportService.getReportsByType(Report.ReportType.SALES));
        model.addAttribute("paymentReports", reportService.getReportsByType(Report.ReportType.PAYMENT));
        return "reports/admin-reports";
    }

    @PostMapping("/admin/generate")
    public String generateAdminReport(@RequestParam Report.ReportType reportType,
                                    @RequestParam Report.ReportPeriod period,
                                    Authentication authentication,
                                    Model model) {
        try {
            String generatedBy = authentication.getName();
            switch (reportType) {
                case INVENTORY:
                    reportService.generateInventoryReport(period, generatedBy);
                    break;
                case SALES:
                    reportService.generateSalesReport(period, generatedBy);
                    break;
                case PAYMENT:
                    reportService.generatePaymentReport(period, generatedBy);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid report type");
            }
            return "redirect:/reports/admin";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to generate report: " + e.getMessage());
            model.addAttribute("inventoryReports", reportService.getReportsByType(Report.ReportType.INVENTORY));
            model.addAttribute("salesReports", reportService.getReportsByType(Report.ReportType.SALES));
            model.addAttribute("paymentReports", reportService.getReportsByType(Report.ReportType.PAYMENT));
            return "reports/admin-reports";
        }
    }

    // Staff Reports - Inventory and Sales only
    @GetMapping("/staff")
    public String staffReports(Model model) {
        model.addAttribute("inventoryReports", reportService.getReportsByType(Report.ReportType.INVENTORY));
        model.addAttribute("salesReports", reportService.getReportsByType(Report.ReportType.SALES));
        return "reports/staff-reports";
    }

    @PostMapping("/staff/generate")
    public String generateStaffReport(@RequestParam Report.ReportType reportType,
                                    @RequestParam Report.ReportPeriod period,
                                    Authentication authentication) {
        String generatedBy = authentication.getName();
        // Staff can only generate inventory and sales reports
        if (reportType == Report.ReportType.INVENTORY || reportType == Report.ReportType.SALES) {
            switch (reportType) {
                case INVENTORY:
                    reportService.generateInventoryReport(period, generatedBy);
                    break;
                case SALES:
                    reportService.generateSalesReport(period, generatedBy);
                    break;
            }
        }
        return "redirect:/reports/staff";
    }

    // Supplier Reports - Inventory only
    @GetMapping("/supplier")
    public String supplierReports(Model model) {
        model.addAttribute("inventoryReports", reportService.getReportsByType(Report.ReportType.INVENTORY));
        return "reports/supplier-reports";
    }

    @PostMapping("/supplier/generate")
    public String generateSupplierReport(@RequestParam Report.ReportPeriod period,
                                       Authentication authentication) {
        String generatedBy = authentication.getName();
        // Suppliers can only generate inventory reports
        reportService.generateInventoryReport(period, generatedBy);
        return "redirect:/reports/supplier";
    }

    // View specific report
    @GetMapping("/view/{id}")
    public String viewReport(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            return reportService.getReportById(id)
                    .map(report -> {
                        try {
                            // Check if user has permission to view this report
                            if (hasPermissionToViewReport(report, authentication)) {
                                model.addAttribute("report", report);
                                return "reports/view-report";
                            } else {
                                return "error/403";
                            }
                        } catch (Exception e) {
                            model.addAttribute("error", e.getMessage());
                            return "error/500";
                        }
                    })
                    .orElse("error/404");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error/500";
        }
    }

    // Delete report (Admin only)
    @PostMapping("/admin/delete/{id}")
    public String deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return "redirect:/reports/admin";
    }

    private boolean hasPermissionToViewReport(Report report, Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        switch (report.getReportType()) {
            case INVENTORY:
                return role.equals("ROLE_ADMIN") || role.equals("ROLE_STAFF") || role.equals("ROLE_SUPPLIER");
            case SALES:
                return role.equals("ROLE_ADMIN") || role.equals("ROLE_STAFF");
            case PAYMENT:
                return role.equals("ROLE_ADMIN");
            default:
                return false;
        }
    }
}
