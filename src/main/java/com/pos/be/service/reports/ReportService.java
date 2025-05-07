package com.pos.be.service.reports;

import com.pos.be.constants.PaymentMethod;
import com.pos.be.constants.TransactionStatus;
import com.pos.be.dto.sales.*;
import com.pos.be.entity.order.ConsignmentStatus;
import com.pos.be.repository.order.ConsignmentRepository;
import com.pos.be.repository.transaction.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService {
    private final TransactionRepository transactionRepository;
    private final ConsignmentRepository consignmentRepository;

    public ReportService(TransactionRepository transactionRepository, ConsignmentRepository consignmentRepository) {
        this.transactionRepository = transactionRepository;
        this.consignmentRepository = consignmentRepository;
    }

    public RevenueDTO getRevenue(String interval, LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end   = to.plusDays(1).atStartOfDay();
        List<Object[]> rows = transactionRepository.sumRevenueByInterval(interval, start, end);
        List<String> labels = rows.stream().map(r -> (String) r[0]).collect(Collectors.toList());
        List<BigDecimal> data = rows.stream().map(r -> (BigDecimal) r[1]).collect(Collectors.toList());
        return new RevenueDTO(labels, data);
    }

    public List<PaymentMethodStatsDTO> getPaymentMethodStats() {
        return transactionRepository.countByPaymentMethod().stream()
                .map(r -> new PaymentMethodStatsDTO((PaymentMethod) r[0], (Long) r[1]))
                .collect(Collectors.toList());
    }

    public List<TransactionStatusStatsDTO> getTransactionStatusStats() {
        return transactionRepository.countByTransactionStatus().stream()
                .map(r -> new TransactionStatusStatsDTO((TransactionStatus) r[0], (Long) r[1]))
                .collect(Collectors.toList());
    }

    public DashboardKPIsDTO getDashboardKPIs() {
        BigDecimal revenue = transactionRepository.sumTotalRevenue();
        Long orders = consignmentRepository.count();
        return new DashboardKPIsDTO(
                revenue != null ? revenue : BigDecimal.ZERO,
                orders
        );
    }

    public List<SalesOverTimeDTO> getSalesOverTime(LocalDate from, LocalDate to) {
        // reuse sumRevenueByInterval with daily granularity, or implement separate query
        List<Object[]> rows = transactionRepository.sumRevenueByInterval("daily", from.atStartOfDay(), to.plusDays(1).atStartOfDay());
        return rows.stream()
                .map(r -> new SalesOverTimeDTO(
                        LocalDate.parse((String) r[0]),
                        (BigDecimal) r[1]
                )).collect(Collectors.toList());
    }

    public OrdersByStatusDTO getOrdersByStatus(String interval, LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end   = to.plusDays(1).atStartOfDay();

        List<Object[]> rows = consignmentRepository.countConsignmentsByStatusInterval(interval, start, end);

        // Build list of period labels:
        List<String> labels = rows.stream()
                .map(r -> (String) r[0])
                .distinct()
                .collect(Collectors.toList());

        // Initialize zero-series for each status
        Map<String, List<Long>> series = new LinkedHashMap<>();
        for (ConsignmentStatus status : EnumSet.of(
                ConsignmentStatus.PENDING,
                ConsignmentStatus.COMPLETED,
                ConsignmentStatus.DISPATCHED,
                ConsignmentStatus.CANCELLED
        )) {
            series.put(status.name(),
                    labels.stream().map(l -> 0L).collect(Collectors.toList())
            );
        }

        // Fill in actual counts
        for (Object[] row : rows) {
            String period = (String) row[0];
            ConsignmentStatus status = (ConsignmentStatus) row[1];
            Long count = (Long) row[2];
            int idx = labels.indexOf(period);
            series.get(status.name()).set(idx, count);
        }

        return new OrdersByStatusDTO(labels, series);
    }

    public TransactionStatusOverTimeDTO getTransactionStatusOverTime(
            String interval, LocalDate from, LocalDate to) {

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end   = to.plusDays(1).atStartOfDay();

        // 1) Fetch raw rows
        List<Object[]> rows = transactionRepository.countTransactionByStatusInterval(interval, start, end);

        // 2) Build ordered list of period labels
        List<String> labels = rows.stream()
                .map(r -> (String) r[0])
                .distinct()
                .collect(Collectors.toList());

        // 3) Pre-populate series for each real status + "UNKNOWN"
        Map<String, List<Long>> series = new LinkedHashMap<>();
        // real statuses
        for (TransactionStatus ts : TransactionStatus.values()) {
            series.put(ts.name(),
                    labels.stream().map(l -> 0L).collect(Collectors.toList()));
        }
        // unknown bucket
        series.put("UNKNOWN",
                labels.stream().map(l -> 0L).collect(Collectors.toList()));

        // 4) Fill real counts (or UNKNOWN if status==null)
        for (Object[] row : rows) {
            String period = (String) row[0];
            TransactionStatus status = (TransactionStatus) row[1];
            Long count = (Long) row[2];

            String key = (status != null ? status.name() : "UNKNOWN");
            int idx = labels.indexOf(period);
            series.get(key).set(idx, count);
        }

        return new TransactionStatusOverTimeDTO(labels, series);
    }

    public PaymentMethodChartDTO getPaymentMethodsChart(
            String interval, LocalDate from, LocalDate to) {

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end   = to.plusDays(1).atStartOfDay();

        List<Object[]> rows = transactionRepository.aggregatePaymentMethodsByInterval(interval, start, end);

        // 1) Collect labels in order
        LinkedHashSet<String> labelSet = new LinkedHashSet<>();
        for (Object[] r : rows) {
            labelSet.add((String) r[0]);
        }
        List<String> labels = new ArrayList<>(labelSet);

        // 2) Initialize series map, including UNKNOWN
        Map<String, List<Long>> series = new LinkedHashMap<>();
        for (Object[] r : rows) {
            String pm = (String) r[1];
            String key = pm != null ? pm : "UNKNOWN";
            series.putIfAbsent(key,
                    labels.stream().map(l -> 0L).collect(Collectors.toList()));
        }

        // 3) Populate counts
        for (Object[] r : rows) {
            String label = (String) r[0];
            String pm    = (String) r[1];
            String key   = pm != null ? pm : "UNKNOWN";

            Number rawCount = (Number) r[2];
            long count = rawCount == null ? 0L : rawCount.longValue();

            int idx = labels.indexOf(label);
            series.get(key).set(idx, count);
        }

        return new PaymentMethodChartDTO(labels, series);
    }
}
