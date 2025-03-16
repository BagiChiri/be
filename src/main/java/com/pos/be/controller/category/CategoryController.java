package com.pos.be.controller.category;

import com.pos.be.dto.category.CategoryDTO;
import com.pos.be.service.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    @PutMapping//reel about put and patch
    public ResponseEntity<?> update(
            @RequestBody CategoryDTO request
    ) {
        try {
            return categoryService.update(request);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
        }
    }

    @GetMapping("/by_name")
    public ResponseEntity<?> getAllCategories(
            @RequestParam(required = false) String query,
            Pageable pageable
    ) {
        return categoryService.getAll(query, pageable);
    }

    //here it starts today
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
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            return categoryService.delete(id);
        } catch (DataIntegrityViolationException e) {
            // Handle cases where the foreign key constraint is violated
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Cannot delete category because it is referenced by other records.");
        } catch (Exception e) {
            // Handle other unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while deleting the category.");
        }
    }

}
