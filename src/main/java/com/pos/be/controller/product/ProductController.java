package com.pos.be.controller.product;

import com.pos.be.dto.product.ProductDTO;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> add(@RequestBody @Valid ProductDTO request) {
        return productService.saveWithImages(request, null);
    }

    @PostMapping(value = "/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> addProductWithImages(
            @RequestPart("product") @Valid ProductDTO productDTO,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        return productService.saveWithImages(productDTO, images);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid ProductDTO request) {
        request.setId(id);
        return productService.updateWithImages(request, null);
    }

    @PutMapping(value = "/{id}/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> updateProductWithImages(
            @PathVariable Long id,
            @RequestPart("product") @Valid ProductDTO productDTO,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        productDTO.setId(id);
        return productService.updateWithImages(productDTO, images);
    }

    @GetMapping("/by_id/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return productService.get(id);
    }

    @GetMapping("/detail/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> getDetailedProduct(@PathVariable Long id) {
        return productService.getDetailedProduct(id);
    }

    @GetMapping("/by_name")
    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Page<ProductDTO> get(@RequestParam(required = false) String query, Pageable pageable) {
        return productService.getProducts(query, pageable);
    }

    @GetMapping("/by_category/{categoryId}")
    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Page<ProductDTO> getByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) String query,
            Pageable pageable
    ) {
        return productService.getProductsByCategory(categoryId, query, pageable);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return productService.delete(id);
    }
}