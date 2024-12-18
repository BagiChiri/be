package com.pos.be.controller.product;

import com.pos.be.dto.product.CustomOptionsDTO;
import com.pos.be.service.product.CustomOptionsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/custom-options")
public class CustomOptionsController {
    private final CustomOptionsService customOptionsService;

    @Autowired
    CustomOptionsController(CustomOptionsService customOptionsService) {
        this.customOptionsService = customOptionsService;
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody CustomOptionsDTO customOptionsDTO) {
        return customOptionsService.save(customOptionsDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return customOptionsService.get(id);
    }

    @GetMapping
    public ResponseEntity<?> get() {
        return customOptionsService.getAll();
    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody CustomOptionsDTO customOptionsDTO) {
        return customOptionsService.update(customOptionsDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return customOptionsService.delete(id);
    }
}
