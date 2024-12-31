package com.pos.be.service.product;

import com.pos.be.dto.product.ProductDTO;
import com.pos.be.entity.category.Category;
import com.pos.be.entity.product.Product;
import com.pos.be.repository.category.CategoryRepository;
import com.pos.be.repository.product.ProductRepository;
import com.pos.be.service.category.CategoryService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
//
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
//
    public ResponseEntity<?> delete(
            Long id
    ) {
        productRepository.deleteById(id);
        return ResponseEntity.ok(id);
    }

    public Page<ProductDTO> getProducts(String name, Pageable pageable) {
        Page<Object[]> rawPage = productRepository.findRawProductData(name, pageable);
        List<Object[]> rawData = rawPage.getContent();

        List<ProductDTO> products = rawData.stream().map(row -> {
            Long productId = ((Number) row[0]).longValue();
            String productName = (String) row[1];
            String description = (String) row[2];
            Double price = (Double) row[3];
            Integer quantity = (Integer) row[4];
            String categoryIdsRaw = (String) row[5];

            Set<Long> categoryIds = Arrays.stream(categoryIdsRaw.split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());

            return ProductDTO.builder()
                    .id(productId)
                    .name(productName)
                    .description(description)
                    .price(price)
                    .quantity(quantity)
                    .categoryIds(categoryIds)
                    .build();
        }).toList();

        return new PageImpl<>(products, pageable, rawPage.getTotalElements());
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
//                .categoryIds(categoryIds)
                .build();
    }

    private Product convertToEntity(
            ProductDTO productDTO
    ) {
        Set<Category> categorySet = new HashSet<>();
//        categoryService.findAllByIds(productDTO.getCategoryIds()).forEach(
//                categorySet::add
//        );
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