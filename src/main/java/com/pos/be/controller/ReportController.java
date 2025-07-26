package com.pos.be.controller;

import com.pos.be.dto.sales.*;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.service.reports.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAuthority('" + Permissions.FULL_ACCESS + "')")
public class ReportController {

    private final ReportService svc;

    public ReportController(ReportService svc) {
        this.svc = svc;
    }

    @GetMapping("/revenue")
    public ResponseEntity<RevenueDTO> getRevenue(
            @RequestParam(defaultValue = "daily") String interval,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(svc.getRevenue(interval, fromDate, toDate));
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<PaymentMethodChartDTO> paymentMethods(
            @RequestParam(defaultValue = "daily") String interval,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(svc.getPaymentMethodsChart(interval, from, to));
    }

    @GetMapping("/transaction-statuses")
    public ResponseEntity<List<TransactionStatusStatsDTO>> getTransactionStatuses() {
        return ResponseEntity.ok(svc.getTransactionStatusStats());
    }

    @GetMapping("/dashboard-kpis")
    public ResponseEntity<DashboardKPIsDTO> getKPIs() {
        return ResponseEntity.ok(svc.getDashboardKPIs());
    }

    @GetMapping("/sales-over-time")
    public ResponseEntity<List<SalesOverTimeDTO>> getSalesOverTime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(svc.getSalesOverTime(fromDate, toDate));
    }

    @GetMapping("/orders-by-status")
    public ResponseEntity<OrdersByStatusDTO> getOrdersByStatus(
            @RequestParam(defaultValue = "daily") String interval,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(
                svc.getOrdersByStatus(interval, fromDate, toDate)
        );
    }

    @GetMapping("/transactions-by-status")
    public ResponseEntity<TransactionStatusOverTimeDTO> getTransactionsByStatus(
            @RequestParam(defaultValue = "daily") String interval,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(
                svc.getTransactionStatusOverTime(interval, fromDate, toDate)
        );
    }
}
