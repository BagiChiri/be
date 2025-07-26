/*
 * com.pos.be.service.transaction.impl.TransactionServiceImpl.java
 */
package com.pos.be.service;

import com.pos.be.constants.PaymentMethod;
import com.pos.be.constants.TransactionStatus;
import com.pos.be.entity.transaction.Transaction;
import com.pos.be.exception.PermissionDeniedException;
import com.pos.be.exception.ResourceNotFoundException;
import com.pos.be.paymetngateway.CardPaymentGateway;
import com.pos.be.repository.transaction.TransactionRepository;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.security.rbac.SecurityUtils;
import com.pos.be.specification.GenericSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CardPaymentGateway cardPaymentGateway;

    @Override
    public Page<Transaction> getTransactions(Map<String, String> filters, Pageable pageable) {
        if (!SecurityUtils.hasPermission(Permissions.READ_TRANSACTION)) {
            throw new PermissionDeniedException("You don't have permission to view transactions");
        }
        Specification<Transaction> spec = new GenericSpecification<>(filters, Transaction.class);
        return transactionRepository.findAll(spec, pageable);
    }

    @Override
    public Transaction getTransactionById(Long id) {
        if (!SecurityUtils.hasPermission(Permissions.READ_TRANSACTION)) {
            throw new PermissionDeniedException("You don't have permission to view transactions");
        }
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

    @Override
    @Transactional
    public Transaction createTransaction(Transaction tx) {
        if (!SecurityUtils.hasPermission(Permissions.CREATE_TRANSACTION)) {
            throw new PermissionDeniedException("You don't have permission to create transactions");
        }
        tx.setTransactionDate(LocalDateTime.now());

        if (tx.getPaymentMethod() == PaymentMethod.CARD) {
            String ref = cardPaymentGateway.processCardPayment(
                    tx.getCardTrackData(),  // Card track data sent from the front end
                    tx.getPaidAmount()      // Amount for the transaction
            );

            tx.setReferenceNumber(ref);
            tx.setProcessedByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            tx.setStatus(TransactionStatus.COMPLETED);
        }

        return transactionRepository.save(tx);
    }


    @Override
    @Transactional
    public Transaction updateTransaction(Long id, Transaction transaction) {
        if (!SecurityUtils.hasPermission(Permissions.UPDATE_TRANSACTION)) {
            throw new PermissionDeniedException("You don't have permission to update transactions");
        }
        Transaction existing = getTransactionById(id);
        existing.setPaidAmount(transaction.getPaidAmount());
        existing.setChangeAmount(transaction.getChangeAmount());
        existing.setStatus(transaction.getStatus());
        existing.setPaymentMethod(transaction.getPaymentMethod());
        existing.setReferenceNumber(transaction.getReferenceNumber());
        existing.setRemarks(transaction.getRemarks());
        return transactionRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        if (!SecurityUtils.hasPermission(Permissions.DELETE_TRANSACTION)) {
            throw new PermissionDeniedException("You don't have permission to delete transactions");
        }
        Transaction existing = getTransactionById(id);
        transactionRepository.delete(existing);
    }
}
