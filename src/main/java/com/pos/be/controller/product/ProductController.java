package com.pos.be.controller.product;

import com.pos.be.dto.product.ProductDTO;
import com.pos.be.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/by_name")
    public Page<ProductDTO> get(
            @RequestParam(required = false) String query,
            Pageable pageable) {
        return productService.getProducts(query, pageable);
    }


    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getDetailedProduct(
            @PathVariable Long id
    ) {
        return productService.getDetailedProduct(id);
    }

    @GetMapping("/by_id/{id}")
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
