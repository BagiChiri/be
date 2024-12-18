package com.pos.be.controller.product;

import com.pos.be.dto.product.ProductDTO;
import com.pos.be.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = "/api/products"
)
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> add(
            @RequestBody ProductDTO request
    ) {
        return productService.save(request);
    }

    @GetMapping
    public Object get(
            Pageable pageable
    ) {
        return productService.getAll(pageable);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getDetailedProduct(
            @PathVariable Long id
    ) {
        return productService.getDetailedProduct(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id
    ) {
        return productService.get(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @RequestBody ProductDTO request
    ) {
        return productService.update(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id
    ) {
        return productService.delete(id);
    }
}
