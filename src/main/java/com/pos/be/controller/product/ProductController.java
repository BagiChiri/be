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

    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Valid ProductDTO request) {
        return productService.saveWithImages(request, null);
    }

    @PostMapping(value = "/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProductWithImages(
            @RequestPart("product") @Valid ProductDTO productDTO,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        return productService.saveWithImages(productDTO, images);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid ProductDTO request) {
        request.setId(id);
        return productService.updateWithImages(request, null);
    }

    @PutMapping(value = "/{id}/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProductWithImages(
            @PathVariable Long id,
            @RequestPart("product") @Valid ProductDTO productDTO,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        productDTO.setId(id);
        return productService.updateWithImages(productDTO, images);
    }

    @GetMapping("/by_id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return productService.get(id);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getDetailedProduct(@PathVariable Long id) {
        return productService.getDetailedProduct(id);
    }

    @GetMapping("/by_name")
    public Page<ProductDTO> get(@RequestParam(required = false) String query, Pageable pageable) {
        return productService.getProducts(query, pageable);
    }

    @GetMapping("/by_category/{categoryId}")
    public Page<ProductDTO> getByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return productService.getProductsByCategory(categoryId, pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return productService.delete(id);
    }
}
