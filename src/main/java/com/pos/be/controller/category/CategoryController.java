package com.pos.be.controller.category;

import com.pos.be.dto.category.CategoryDTO;
import com.pos.be.service.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> add(
            @Valid @RequestBody CategoryDTO request
    ) {
        try {
            return categoryService.save(request);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        }
    }

    @PutMapping
    public ResponseEntity<?> update(
            @RequestBody CategoryDTO request
    ) {
        try {
            return categoryService.update(request);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(
            @PathVariable Long id
    ) /*throws ResponseStatusException*/ {
        try {
            return categoryService.get(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id
    ) {
        return categoryService.delete(id);
    }
}
