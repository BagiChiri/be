/*
 * com.pos.be.service.transaction.TransactionService.java
 */
package com.pos.be.service;

import com.pos.be.entity.transaction.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface TransactionService {
    Page<Transaction> getTransactions(Map<String, String> filters, Pageable pageable);
    Transaction getTransactionById(Long id);
    Transaction createTransaction(Transaction transaction);
    Transaction updateTransaction(Long id, Transaction transaction);
    void deleteTransaction(Long id);
}