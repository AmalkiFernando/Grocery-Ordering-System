package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.repository.ReportRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final ProductService productService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    public ReportService(ReportRepository reportRepository, ProductService productService, 
                        OrderService orderService, PaymentService paymentService) {
        this.reportRepository = reportRepository;
        this.productService = productService;
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.objectMapper = new ObjectMapper();
    }

    public Report generateInventoryReport(Report.ReportPeriod period, String generatedBy) {
        LocalDateTime[] periodRange = getPeriodRange(period);
        LocalDateTime startDate = periodRange[0];
        LocalDateTime endDate = periodRange[1];

        List<Product> products = productService.getAll();
        List<Product> lowStockProducts = productService.getLowStock();

        Map<String, Object> reportData = new HashMap<>();
        reportData.put("totalProducts", products.size());
        reportData.put("totalValue", calculateTotalInventoryValue(products));
        reportData.put("lowStockCount", lowStockProducts.size());
        reportData.put("products", products);
        reportData.put("lowStockProducts", lowStockProducts);
        reportData.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        Report report = new Report(Report.ReportType.INVENTORY, period, startDate, endDate);
        report.setGeneratedBy(generatedBy);
        try {
            report.setReportData(objectMapper.writeValueAsString(reportData));
        } catch (JsonProcessingException e) {
            report.setReportData("{}");
        }

        return reportRepository.save(report);
    }

    public Report generateSalesReport(Report.ReportPeriod period, String generatedBy) {
        LocalDateTime[] periodRange = getPeriodRange(period);
        LocalDateTime startDate = periodRange[0];
        LocalDateTime endDate = periodRange[1];

        List<Order> completedOrders = orderService.getCompletedOrdersBetween(startDate, endDate);
        Double totalSales = orderService.getTotalSalesBetween(startDate, endDate);

        Map<String, Object> reportData = new HashMap<>();
        reportData.put("totalOrders", completedOrders.size());
        reportData.put("totalSales", totalSales);
        reportData.put("averageOrderValue", completedOrders.isEmpty() ? 0 : totalSales / completedOrders.size());
        reportData.put("orders", completedOrders);
        reportData.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        Report report = new Report(Report.ReportType.SALES, period, startDate, endDate);
        report.setGeneratedBy(generatedBy);
        try {
            report.setReportData(objectMapper.writeValueAsString(reportData));
        } catch (JsonProcessingException e) {
            report.setReportData("{}");
        }

        return reportRepository.save(report);
    }

    public Report generatePaymentReport(Report.ReportPeriod period, String generatedBy) {
        LocalDateTime[] periodRange = getPeriodRange(period);
        LocalDateTime startDate = periodRange[0];
        LocalDateTime endDate = periodRange[1];

        List<Payment> completedPayments = paymentService.getCompletedPaymentsBetween(startDate, endDate);
        Double totalPayments = paymentService.getTotalPaymentsBetween(startDate, endDate);

        Map<String, Object> reportData = new HashMap<>();
        reportData.put("totalPayments", completedPayments.size());
        reportData.put("totalAmount", totalPayments);
        reportData.put("payments", completedPayments);
        reportData.put("paymentMethodBreakdown", getPaymentMethodBreakdown(completedPayments));
        reportData.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        Report report = new Report(Report.ReportType.PAYMENT, period, startDate, endDate);
        report.setGeneratedBy(generatedBy);
        try {
            report.setReportData(objectMapper.writeValueAsString(reportData));
        } catch (JsonProcessingException e) {
            report.setReportData("{}");
        }

        return reportRepository.save(report);
    }

    public List<Report> getReportsByType(Report.ReportType reportType) {
        return reportRepository.findByReportTypeOrderByGeneratedDateDesc(reportType);
    }

    public List<Report> getReportsByTypeAndPeriod(Report.ReportType reportType, Report.ReportPeriod period) {
        return reportRepository.findByReportTypeAndPeriodOrderByGeneratedDateDesc(reportType, period);
    }

    public Optional<Report> getReportById(Long id) {
        try {
            return reportRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving report: " + e.getMessage(), e);
        }
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    private LocalDateTime[] getPeriodRange(Report.ReportPeriod period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        LocalDateTime endDate = now;

        switch (period) {
            case DAILY:
                startDate = now.toLocalDate().atStartOfDay();
                break;
            case MONTHLY:
                startDate = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                break;
            case YEARLY:
                startDate = now.withDayOfYear(1).toLocalDate().atStartOfDay();
                break;
            default:
                startDate = now.minusDays(1);
        }

        return new LocalDateTime[]{startDate, endDate};
    }

    private BigDecimal calculateTotalInventoryValue(List<Product> products) {
        return products.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getQuantityInStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<String, Integer> getPaymentMethodBreakdown(List<Payment> payments) {
        Map<String, Integer> breakdown = new HashMap<>();
        for (Payment payment : payments) {
            String method = payment.getPaymentMethod().toString();
            breakdown.put(method, breakdown.getOrDefault(method, 0) + 1);
        }
        return breakdown;
    }
}
