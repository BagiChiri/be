package com.pos.be.controller.transaction;

import com.pos.be.dto.transactions.TransactionDTO;
import com.pos.be.entity.transaction.Transaction;
import com.pos.be.mappers.TransactionMapper;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    public TransactionController(TransactionService transactionService, TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('" + Permissions.READ_TRANSACTION + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<Page<Transaction>> getTransactions(
            @RequestParam Map<String, String> filters,
            Pageable pageable) {
        Page<Transaction> page = transactionService.getTransactions(filters, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.READ_TRANSACTION + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        Transaction tx = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transactionMapper.toDTO(tx));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + Permissions.CREATE_TRANSACTION + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO dto) {
        Transaction entity = transactionMapper.toEntity(dto);
        Transaction created = transactionService.createTransaction(entity);
        return ResponseEntity.ok(transactionMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_TRANSACTION + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionDTO dto) {
        Transaction entity = transactionMapper.toEntity(dto);
        Transaction updated = transactionService.updateTransaction(id, entity);
        return ResponseEntity.ok(transactionMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.DELETE_TRANSACTION + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}

