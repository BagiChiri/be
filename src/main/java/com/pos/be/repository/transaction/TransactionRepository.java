package com.pos.be.repository.transaction;

import com.pos.be.entity.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    @Query("""
              SELECT 
                CASE 
                  WHEN :interval = 'daily' THEN FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m-%d')
                  WHEN :interval = 'monthly' THEN FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m')
                  WHEN :interval = 'yearly' THEN FUNCTION('DATE_FORMAT', t.transactionDate, '%Y')
                END,
                SUM(t.totalAmount)
              FROM Transaction t
              WHERE t.transactionDate >= :fromDate AND t.transactionDate < :toDate
              GROUP BY 
                CASE 
                  WHEN :interval = 'daily' THEN FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m-%d')
                  WHEN :interval = 'monthly' THEN FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m')
                  WHEN :interval = 'yearly' THEN FUNCTION('DATE_FORMAT', t.transactionDate, '%Y')
                END
              ORDER BY 1
            """)
    List<Object[]> sumRevenueByInterval(String interval, LocalDateTime fromDate, LocalDateTime toDate);

    @Query("SELECT t.paymentMethod, COUNT(t) FROM Transaction t GROUP BY t.paymentMethod")
    List<Object[]> countByPaymentMethod();

    @Query("SELECT t.status, COUNT(t) FROM Transaction t GROUP BY t.status")
    List<Object[]> countByTransactionStatus();

    @Query("SELECT SUM(t.totalAmount) FROM Transaction t")
    BigDecimal sumTotalRevenue();

    @Query("""
              SELECT 
                CASE 
                  WHEN :interval = 'daily'   THEN FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m-%d')
                  WHEN :interval = 'monthly' THEN FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m')
                  WHEN :interval = 'yearly'  THEN FUNCTION('DATE_FORMAT', t.transactionDate, '%Y')
                END,
                t.status,
                COUNT(t)
              FROM Transaction t
              WHERE t.transactionDate >= :fromDate
                AND t.transactionDate <  :toDate
              GROUP BY 
                CASE 
                  WHEN :interval = 'daily'   THEN FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m-%d')
                  WHEN :interval = 'monthly' THEN FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m')
                  WHEN :interval = 'yearly'  THEN FUNCTION('DATE_FORMAT', t.transactionDate, '%Y')
                END,
                t.status
              ORDER BY 
                CASE 
                  WHEN :interval = 'daily'   THEN FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m-%d')
                  WHEN :interval = 'monthly' THEN FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m')
                  WHEN :interval = 'yearly'  THEN FUNCTION('DATE_FORMAT', t.transactionDate, '%Y')
                END
            """)
    List<Object[]> countTransactionByStatusInterval(
            @Param("interval") String interval,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query(value = """
                SELECT 
                    DATE_FORMAT(t.transaction_date,
                        CASE
                            WHEN :interval = 'daily' THEN '%Y-%m-%d'
                            WHEN :interval = 'monthly' THEN '%Y-%m'
                            WHEN :interval = 'yearly' THEN '%Y'
                        END
                    ) AS label,
                    t.payment_method,
                    COUNT(*) AS count
                FROM transactions t
                WHERE t.transaction_date >= :from AND t.transaction_date < :to
                GROUP BY label, t.payment_method
                ORDER BY label
            """, nativeQuery = true)
    List<Object[]> aggregatePaymentMethodsByInterval(
            @Param("interval") String interval,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Modifying
    @Query("DELETE FROM Transaction tx WHERE tx.consignment.consignmentId = :consignmentId")
    void deleteByConsignmentId(@Param("consignmentId") Long consignmentId);
}
