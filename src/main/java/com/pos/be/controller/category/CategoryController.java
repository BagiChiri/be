package com.pos.be.controller.category;

import com.pos.be.dto.category.CategoryDTO;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.service.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('" + Permissions.CREATE_CATEGORY + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> add(@Valid @RequestBody CategoryDTO request) {
        return categoryService.save(request);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_CATEGORY + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> update(@RequestBody CategoryDTO request) {
        return categoryService.update(request);
    }

    @GetMapping("/by_name")
    @PreAuthorize("hasAuthority('" + Permissions.READ_CATEGORY + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> getAllCategories(@RequestParam(required = false) String query, Pageable pageable) {
        return categoryService.getAll(query, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.READ_CATEGORY + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<CategoryDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.get(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.DELETE_CATEGORY + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return categoryService.delete(id);
    }
}
