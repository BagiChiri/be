package com.pos.be.service.product;

import com.pos.be.dto.product.ProductDTO;
import com.pos.be.entity.category.Category;
import com.pos.be.entity.product.Product;
import com.pos.be.repository.category.CategoryRepository;
import com.pos.be.repository.product.ProductRepository;
import com.pos.be.service.category.CategoryService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public ResponseEntity<?> save(
            ProductDTO productDTO
    ) {
        Set<Long> wrongIds = categoryService.existsById(productDTO.getCategoryIds());
        if (!wrongIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Category id: " + wrongIds + " doesn't exist.");
        }
        var product = productRepository.save(convertToEntity(productDTO));
        return ResponseEntity.ok(convertToDTO(product));
    }

    public ResponseEntity<?> get(
            Long id
    ) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(convertToDTO(product.get()));
        }
        return ResponseEntity.ok("Product With Id: " + id + " Doesn't Exists.");
    }

    public ResponseEntity<?> getAll(
            Pageable pageable
    ) {
        List<ProductDTO> productDTOS = new ArrayList<>();
        productRepository.findAll().forEach(
                product -> {
                    Set<Long> categoryIds = new HashSet<>();
                    product.getCategories().forEach(
                            p -> categoryIds.add(p.getId())
                    );
                    productDTOS.add(ProductDTO.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .description(product.getDescription())
                            .price(product.getPrice())
                            .categoryIds(categoryIds)
                            .quantity(product.getQuantity())
                            .build());
                }
        );
        return ResponseEntity.ok(productDTOS);
    }

//    public Object getAll() {
//        List<ProductDTO> productDTOS = new ArrayList<>();
//        productRepository.findAll().forEach(
//                product -> {
//                    String categories;
//                    Iterable iterable = product.getCategories();
//                    categoryRepository.findAllById(iterable).forEach(
//                            category -> {
//                                categories = categories + category.toString()
//                            }
//                    );
//                    ProductData productData = new ProductData(
//                            product.getId(),
//                            product.getName(),
//                            product.getDescription(),
//                            product.getPrice(),
//                            product.getCategories()
//                    )
//                }
//        );

//        return productRepository.getProductIntro();
//    }

    public ResponseEntity<?> update(
            ProductDTO dto
    ) {
        Set<Category> categories = new HashSet<>();
        categoryService.findAllByIds(dto.getCategoryIds()).forEach(
                categories::add
        );
        Optional<Product> productUpdate = productRepository.findById(dto.getId()).map(
                product -> {
                    product.setName(dto.getName());
                    product.setDescription(dto.getDescription());
                    product.setPrice(dto.getPrice());
                    product.setCategories(categories);
                    return product;
                }
        );
        if (productUpdate.isPresent()) {
            productRepository.save(productUpdate.get());
            return ResponseEntity.ok("Product Update.");
        } else {
            return ResponseEntity.ok("Product With Id: " + dto.getId() + " Doesn't Exists.");
        }
    }

    public ResponseEntity<?> delete(
            Long id
    ) {
        productRepository.deleteById(id);
        return ResponseEntity.ok(id);
    }

    private ProductDTO convertToDTO(
            Product product
    ) {
        Set<Long> categoryIds = new HashSet<>();
        product.getCategories().forEach(
                p -> categoryIds.add(p.getId())
        );
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryIds(categoryIds)
                .build();
    }

    private Product convertToEntity(
            ProductDTO productDTO
    ) {
        Set<Category> categorySet = new HashSet<>();
        categoryService.findAllByIds(productDTO.getCategoryIds()).forEach(
                categorySet::add
        );
        return Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .categories(categorySet)
                .build();
    }

    public ResponseEntity<?> getDetailedProduct(
            Long id
    ) {
        return ResponseEntity.ok(productRepository.getProductDetailsById(id));
    }
}