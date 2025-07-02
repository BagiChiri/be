//package com.pos.be.controller.product;
//
//import com.pos.be.dto.product.ProductDTO;
//import com.pos.be.security.rbac.Permissions;
//import com.pos.be.service.product.ProductService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/products")
//public class ProductController {
//
//    private final ProductService productService;
//
//    @PostMapping
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> add(@RequestBody @Valid ProductDTO request) {
//        return productService.saveWithImages(request, null);
//    }
//
//    @PostMapping(value = "/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> addProductWithImages(
//            @RequestPart("product") @Valid ProductDTO productDTO,
//            @RequestPart(value = "images", required = false) MultipartFile[] images) {
//        return productService.saveWithImages(productDTO, images);
//    }
//
//    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid ProductDTO request) {
//        request.setId(id);
//        return productService.updateWithImages(request, null);
//    }
//
//    @PutMapping(value = "/{id}/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> updateProductWithImages(
//            @PathVariable Long id,
//            @RequestPart("product") @Valid ProductDTO productDTO,
//            @RequestPart(value = "images", required = false) MultipartFile[] images) {
//        productDTO.setId(id);
//        return productService.updateWithImages(productDTO, images);
//    }
//
//    @GetMapping("/by_id/{id}")
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> getById(@PathVariable Long id) {
//        return productService.get(id);
//    }
//
//    @GetMapping("/detail/{id}")
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> getDetailedProduct(@PathVariable Long id) {
//        return productService.getDetailedProduct(id);
//    }
//
//    @GetMapping("/by_name")
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public Page<ProductDTO> get(@RequestParam(required = false) String query, Pageable pageable) {
//        return productService.getProducts(query, pageable);
//    }
//
//    @GetMapping("/by_category/{categoryId}")
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public Page<ProductDTO> getByCategory(
//            @PathVariable Long categoryId,
//            @RequestParam(required = false) String query,
//            Pageable pageable
//    ) {
//        return productService.getProductsByCategory(categoryId, query, pageable);
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> delete(@PathVariable Long id) {
//        return productService.delete(id);
//    }
//}
package com.pos.be.controller.product;

import com.pos.be.dto.product.ProductDTO;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAuthority('" + Permissions.CREATE_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> add(@RequestBody @Valid ProductDTO request) {
        try {
            return productService.saveWithImages(request, null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while adding product: " + ex.getMessage());
        }
    }

    @PostMapping(value = "/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('" + Permissions.CREATE_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> addProductWithImages(
            @RequestPart("product") @Valid ProductDTO productDTO,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        try {
            return productService.saveWithImages(productDTO, images);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while adding product with images: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid ProductDTO request) {
        try {
            request.setId(id);
            return productService.updateWithImages(request, null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while updating product: " + ex.getMessage());
        }
    }

    @PutMapping(value = "/{id}/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> updateProductWithImages(
            @PathVariable Long id,
            @RequestPart("product") @Valid ProductDTO productDTO,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        try {
            productDTO.setId(id);
            return productService.updateWithImages(productDTO, images);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while updating product with images: " + ex.getMessage());
        }
    }

    @GetMapping("/by_id/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.READ_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return productService.get(id);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while getting product: " + ex.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.READ_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> getDetailedProduct(@PathVariable Long id) {
        try {
            return productService.getDetailedProduct(id);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while getting detailed product: " + ex.getMessage());
        }
    }

    @GetMapping("/by_name")
    @PreAuthorize("hasAuthority('" + Permissions.READ_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> get(@RequestParam(required = false) String query, Pageable pageable) {
        try {
            Page<ProductDTO> page = productService.getProducts(query, pageable);
            return ResponseEntity.ok(page);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while getting products: " + ex.getMessage());
        }
    }

    /**
     * GET /api/products?ids=1&ids=2&ids=3
     * Returns exactly those products as DTOs.
     */
    @GetMapping("/ordered_products")
    public ResponseEntity<List<ProductDTO>> getByIds(
            @RequestParam List<Long> ids
    ) {
        if (ids.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }
        List<ProductDTO> dtos = productService.findAllById(ids);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/by_category/{categoryId}")
    @PreAuthorize("hasAuthority('" + Permissions.READ_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> getByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) String query,
            Pageable pageable) {
        try {
            return productService.getProductsByCategory(categoryId, query, pageable);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while getting products by category: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.DELETE_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            return productService.delete(id);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while deleting product: " + ex.getMessage());
        }
    }
}
