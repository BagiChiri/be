package com.pos.be.controller.product;

import com.pos.be.dto.product.ProductDTO;
import com.pos.be.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // Create product using JSON payload (without file upload)
    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Valid ProductDTO request) {
        return productService.saveWithImages(request, null);
    }

    // Create product with image file upload (multipart/form-data)
    @PostMapping(value = "/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProductWithImages(
            @RequestPart("product") @Valid ProductDTO productDTO,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        return productService.saveWithImages(productDTO, images);
    }

    // Update product using JSON payload only (no image file upload)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid ProductDTO request) {
        request.setId(id);
        return productService.updateWithImages(request, null);
    }

    // Update product with image file upload (multipart/form-data)
    @PutMapping(value = "/{id}/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProductWithImages(
            @PathVariable Long id,
            @RequestPart("product") @Valid ProductDTO productDTO,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        productDTO.setId(id);
        return productService.updateWithImages(productDTO, images);
    }

    // Retrieve a product by id
    @GetMapping("/by_id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return productService.get(id);
    }

    // Retrieve detailed product information
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getDetailedProduct(@PathVariable Long id) {
        return productService.getDetailedProduct(id);
    }

    // Retrieve products with optional name filtering
    @GetMapping("/by_name")
    public Page<ProductDTO> get(@RequestParam(required = false) String query, Pageable pageable) {
        return productService.getProducts(query, pageable);
    }

    // Retrieve products by category id
    @GetMapping("/by_category/{categoryId}")
    public Page<ProductDTO> getByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return productService.getProductsByCategory(categoryId, pageable);
    }

    // Delete a product by id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return productService.delete(id);
    }
}
