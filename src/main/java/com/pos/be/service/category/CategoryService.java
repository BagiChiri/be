////package com.pos.be.service.category;
////
////import com.pos.be.dto.category.CategoryDTO;
////import com.pos.be.entity.category.Category;
////import com.pos.be.repository.category.CategoryRepository;
////import lombok.RequiredArgsConstructor;
////import org.springframework.dao.DataIntegrityViolationException;
////import org.springframework.data.domain.Page;
////import org.springframework.data.domain.PageRequest;
////import org.springframework.data.domain.Pageable;
////import org.springframework.http.HttpStatus;
////import org.springframework.http.ResponseEntity;
////import org.springframework.stereotype.Service;
////import org.springframework.web.server.ResponseStatusException;
////
////import java.util.*;
////import java.util.stream.Collectors;
////
////@Service
////@RequiredArgsConstructor
////public class CategoryService {
////    private final CategoryRepository categoryRepository;
////
////    public ResponseEntity<?> save(
////            CategoryDTO request
////    ) {
////        var category = convertToEntity(request);
////        try {
////            var categoryDTO = convertToDTO(categoryRepository.save(category));
////            return ResponseEntity.ok(categoryDTO);
////        } catch (Exception e) {
////            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to add category.");
////        }
////    }
////
////    public ResponseEntity<?> update(CategoryDTO request) {
////        Category category = categoryRepository.findById(
////                        request.getId()
////                )
////                .orElseThrow(
////                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category doesn't exist.")
////                );
////        category.setId(request.getId());
////        category.setName(request.getName());
////        categoryRepository.save(category);
////        return ResponseEntity.ok().body("Updated Successfully.");
////    }
////
////    public ResponseEntity<?> getAll(Pageable pageable) {
////        Page<Object[]> categoryPage = categoryRepository.findCategoriesWithProductCounts(pageable);
////
////        List<CategoryDTO> categoryIds = categoryPage.getContent().stream()
////                .map(result -> {
////                    Category category = (Category) result[0];
////                    Long productCount = (Long) result[1];
////                    return CategoryDTO.builder()
////                            .id(category.getId())
////                            .name(category.getName())
////                            .itemCount(productCount.intValue())
////                            .build();
////                })
////                .collect(Collectors.toList());
////
////        Map<String, Object> response = new HashMap<>();
////        response.put("categoryIds", categoryIds);
////        response.put("totalPages", categoryPage.getTotalPages());
////        response.put("totalCategories", categoryPage.getTotalElements());
////        return ResponseEntity.ok(response);
////    }
////
////
////    public ResponseEntity<?> get(Long id) {
////        Category category = categoryRepository
////                .findById(id)
////                .orElseThrow(
////                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category doesn't exists")
////                );
////        CategoryDTO categoryDTO = convertToDTO(category);
////
////        return ResponseEntity.ok(categoryDTO);
////    }
////
////    public ResponseEntity<?> delete(Long id) {
////        if (categoryRepository.existsById(id)) {
////            try {
////                categoryRepository.deleteById(id);
////                return ResponseEntity.ok(id);
////            } catch (DataIntegrityViolationException e) {
////                // Handle foreign key constraint violation
////                return ResponseEntity.status(HttpStatus.CONFLICT)
////                        .body("Cannot delete category because products exist under this category.");
////            }
////        } else {
////            return ResponseEntity.status(HttpStatus.NOT_FOUND)
////                    .body("Category doesn't exist.");
////        }
////    }
////
////
////    public Iterable<Category> findAllByIds(Set<Long> ids) {
////        return categoryRepository.findAllById(ids);
////    }
////
////    private List<CategoryDTO> convertToDTO(List<Category> categoryIds) {
////        return categoryIds.stream()
////                .map(category -> CategoryDTO.builder()
////                        .id(category.getId())
////                        .name(category.getName())
////                        .itemCount(category.getProducts().size()) // Add item count
////                        .build())
////                .collect(Collectors.toList());
////    }
////
////    private CategoryDTO convertToDTO(Category single) {
////        if (single == null) {
////            throw new IllegalArgumentException("Category cannot be null.");
////        }
////        return CategoryDTO.builder()
////                .id(single.getId())
////                .name(single.getName())
////                .build();
////    }
////
////
////    private Category convertToEntity(CategoryDTO categoryDTO) {
////        return Category.builder()
////                .name(categoryDTO.getName())
////                .build();
////    }
////
////    public Set<Long> existsById(Set<Long> categoryIds) {
////        Set<Long> wrongIds = new HashSet<>();
////        for (Long id :
////                categoryIds) {
////            if (!categoryRepository.existsById(id)) {
////                wrongIds.add(id);
////            }
////        }
////        return wrongIds;
////    }
////}
//package com.pos.be.service.category;
//
//import com.pos.be.dto.category.CategoryDTO;
//import com.pos.be.entity.category.Category;
//import com.pos.be.exception.PermissionDeniedException;
//import com.pos.be.repository.category.CategoryRepository;
//import com.pos.be.security.rbac.Permissions;
//import com.pos.be.security.rbac.SecurityUtils;
//import lombok.RequiredArgsConstructor;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class CategoryService {
//    private final CategoryRepository categoryRepository;
//
//    @PreAuthorize("hasAuthority('" + Permissions.CATEGORY_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> save(CategoryDTO request) {
//        // Additional service-level permission check
//        if (!SecurityUtils.hasPermission(Permissions.CATEGORY_MANAGE)) {
//            throw new PermissionDeniedException("You don't have permission to create categories");
//        }
//
//        Category category = convertToEntity(request);
//        try {
//            CategoryDTO categoryDTO = convertToDTO(categoryRepository.save(category));
//            return ResponseEntity.ok(categoryDTO);
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to add category.");
//        }
//    }
//
//    @PreAuthorize("hasAuthority('" + Permissions.CATEGORY_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> update(CategoryDTO request) {
//        // Additional service-level permission check
//        if (!SecurityUtils.hasPermission(Permissions.CATEGORY_MANAGE)) {
//            throw new PermissionDeniedException("You don't have permission to update categories");
//        }
//
//        Category category = categoryRepository.findById(request.getId())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category doesn't exist."));
//        category.setName(request.getName());
//        categoryRepository.save(category);
//        return ResponseEntity.ok("Updated Successfully.");
//    }
//
//    @PreAuthorize("hasAuthority('" + Permissions.CATEGORY_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> getAll(String query, Pageable pageable) {
//        // Additional service-level permission check
//        if (!SecurityUtils.hasPermission(Permissions.CATEGORY_VIEW)) {
//            throw new PermissionDeniedException("You don't have permission to view categories");
//        }
//
//        // Get paginated categories with product counts
//        Page<Object[]> categoryPage = categoryRepository.findCategoriesWithProductCounts(query, pageable);
//
//        // Get total count of all categories (not just the paginated ones)
//        long totalCategories = categoryRepository.count();
//
//        // Get total count of all products across all categories
//        long totalProducts = categoryRepository.sumProductsAcrossAllCategories();
//
//        // Map the results to DTOs
//        List<CategoryDTO> categories = categoryPage.getContent().stream()
//                .map(result -> {
//                    Category category = (Category) result[0];
//                    Long productCount = (Long) result[1];
//                    return CategoryDTO.builder()
//                            .id(category.getId())
//                            .name(category.getName())
//                            .itemCount(productCount.intValue())
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        // Build the response with more descriptive keys
//        Map<String, Object> response = new HashMap<>();
//        response.put("categories", categories); // The paginated list of categories
//        response.put("currentPage", pageable.getPageNumber()); // Current page number
//        response.put("pageSize", pageable.getPageSize()); // Page size
//        response.put("totalCategories", totalCategories); // Total categories in database
//        response.put("totalProducts", totalProducts); // Total products across all categories
//        response.put("totalPages", categoryPage.getTotalPages()); // Total pages available
//        response.put("totalElementsInPage", categoryPage.getNumberOfElements()); // Number of elements in current page
//
//        return ResponseEntity.ok(response);
//    }
//
//
//    @PreAuthorize("hasAuthority('" + Permissions.CATEGORY_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> get(Long id) {
//        // Additional service-level permission check
//        if (!SecurityUtils.hasPermission(Permissions.CATEGORY_VIEW)) {
//            throw new PermissionDeniedException("You don't have permission to view categories");
//        }
//
//        Category category = categoryRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category doesn't exists"));
//        CategoryDTO categoryDTO = convertToDTO(category);
//        return ResponseEntity.ok(categoryDTO);
//    }
//
//    @PreAuthorize("hasAuthority('" + Permissions.CATEGORY_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> delete(Long id) {
//        // Additional service-level permission check
//        if (!SecurityUtils.hasPermission(Permissions.CATEGORY_MANAGE)) {
//            throw new PermissionDeniedException("You don't have permission to delete categories");
//        }
//
//        if (categoryRepository.existsById(id)) {
//            try {
//                categoryRepository.deleteById(id);
//                return ResponseEntity.ok(id);
//            } catch (DataIntegrityViolationException e) {
//                return ResponseEntity.status(HttpStatus.CONFLICT)
//                        .body("Cannot delete category because products exist under this category.");
//            }
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body("Category doesn't exist.");
//        }
//    }
//
//
//    public Iterable<Category> findAllByIds(Set<Long> ids) {
//        return categoryRepository.findAllById(ids);
//    }
//
//    private CategoryDTO convertToDTO(Category category) {
//        if (category == null) {
//            throw new IllegalArgumentException("Category cannot be null.");
//        }
//        return CategoryDTO.builder()
//                .id(category.getId())
//                .name(category.getName())
//                .build();
//    }
//
//    private Category convertToEntity(CategoryDTO categoryDTO) {
//        return Category.builder()
//                .name(categoryDTO.getName())
//                .build();
//    }
//
//    public Set<Long> existsById(Set<Long> categoryIds) {
//        Set<Long> wrongIds = new HashSet<>();
//        for (Long id : categoryIds) {
//            if (!categoryRepository.existsById(id)) {
//                wrongIds.add(id);
//            }
//        }
//        return wrongIds;
//    }
//
//
//}
//package com.pos.be.service.category;
//
//import com.pos.be.dto.category.CategoryDTO;
//import com.pos.be.entity.category.Category;
//import com.pos.be.repository.category.CategoryRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class CategoryService {
//    private final CategoryRepository categoryRepository;
//
//    public ResponseEntity<?> save(
//            CategoryDTO request
//    ) {
//        var category = convertToEntity(request);
//        try {
//            var categoryDTO = convertToDTO(categoryRepository.save(category));
//            return ResponseEntity.ok(categoryDTO);
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to add category.");
//        }
//    }
//
//    public ResponseEntity<?> update(CategoryDTO request) {
//        Category category = categoryRepository.findById(
//                        request.getId()
//                )
//                .orElseThrow(
//                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category doesn't exist.")
//                );
//        category.setId(request.getId());
//        category.setName(request.getName());
//        categoryRepository.save(category);
//        return ResponseEntity.ok().body("Updated Successfully.");
//    }
//
//    public ResponseEntity<?> getAll(Pageable pageable) {
//        Page<Object[]> categoryPage = categoryRepository.findCategoriesWithProductCounts(pageable);
//
//        List<CategoryDTO> categoryIds = categoryPage.getContent().stream()
//                .map(result -> {
//                    Category category = (Category) result[0];
//                    Long productCount = (Long) result[1];
//                    return CategoryDTO.builder()
//                            .id(category.getId())
//                            .name(category.getName())
//                            .itemCount(productCount.intValue())
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("categoryIds", categoryIds);
//        response.put("totalPages", categoryPage.getTotalPages());
//        response.put("totalCategories", categoryPage.getTotalElements());
//        return ResponseEntity.ok(response);
//    }
//
//
//    public ResponseEntity<?> get(Long id) {
//        Category category = categoryRepository
//                .findById(id)
//                .orElseThrow(
//                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category doesn't exists")
//                );
//        CategoryDTO categoryDTO = convertToDTO(category);
//
//        return ResponseEntity.ok(categoryDTO);
//    }
//
//    public ResponseEntity<?> delete(Long id) {
//        if (categoryRepository.existsById(id)) {
//            try {
//                categoryRepository.deleteById(id);
//                return ResponseEntity.ok(id);
//            } catch (DataIntegrityViolationException e) {
//                // Handle foreign key constraint violation
//                return ResponseEntity.status(HttpStatus.CONFLICT)
//                        .body("Cannot delete category because products exist under this category.");
//            }
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body("Category doesn't exist.");
//        }
//    }
//
//
//    public Iterable<Category> findAllByIds(Set<Long> ids) {
//        return categoryRepository.findAllById(ids);
//    }
//
//    private List<CategoryDTO> convertToDTO(List<Category> categoryIds) {
//        return categoryIds.stream()
//                .map(category -> CategoryDTO.builder()
//                        .id(category.getId())
//                        .name(category.getName())
//                        .itemCount(category.getProducts().size()) // Add item count
//                        .build())
//                .collect(Collectors.toList());
//    }
//
//    private CategoryDTO convertToDTO(Category single) {
//        if (single == null) {
//            throw new IllegalArgumentException("Category cannot be null.");
//        }
//        return CategoryDTO.builder()
//                .id(single.getId())
//                .name(single.getName())
//                .build();
//    }
//
//
//    private Category convertToEntity(CategoryDTO categoryDTO) {
//        return Category.builder()
//                .name(categoryDTO.getName())
//                .build();
//    }
//
//    public Set<Long> existsById(Set<Long> categoryIds) {
//        Set<Long> wrongIds = new HashSet<>();
//        for (Long id :
//                categoryIds) {
//            if (!categoryRepository.existsById(id)) {
//                wrongIds.add(id);
//            }
//        }
//        return wrongIds;
//    }
//}
package com.pos.be.service.category;

import com.pos.be.dto.category.CategoryDTO;
import com.pos.be.entity.category.Category;
import com.pos.be.exception.PermissionDeniedException;
import com.pos.be.repository.category.CategoryRepository;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.security.rbac.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @PreAuthorize("hasAuthority('" + Permissions.CATEGORY_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<CategoryDTO> save(CategoryDTO request) {
        if (!SecurityUtils.hasPermission(Permissions.CATEGORY_MANAGE)) {
            throw new PermissionDeniedException("You don't have permission to create categories");
        }

        Category category = convertToEntity(request);
        CategoryDTO categoryDTO = convertToDTO(categoryRepository.save(category));
        return ResponseEntity.ok(categoryDTO);
    }

    @PreAuthorize("hasAuthority('" + Permissions.CATEGORY_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<String> update(CategoryDTO request) {
        if (!SecurityUtils.hasPermission(Permissions.CATEGORY_MANAGE)) {
            throw new PermissionDeniedException("You don't have permission to update categories");
        }

        Category category = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category doesn't exist."));

        category.setName(request.getName());
        categoryRepository.save(category);
        return ResponseEntity.ok("Updated Successfully.");
    }

    @PreAuthorize("hasAuthority('" + Permissions.CATEGORY_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public CategoryDTO get(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category doesn't exist."));
        return convertToDTO(category);
    }

    @PreAuthorize("hasAuthority('" + Permissions.CATEGORY_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<Map<String, Object>> getAll(String query, Pageable pageable) {
        if (!SecurityUtils.hasPermission(Permissions.CATEGORY_VIEW)) {
            throw new PermissionDeniedException("You don't have permission to view categories");
        }

        Page<Object[]> categoryPage = categoryRepository.findCategoriesWithProductCounts(query, pageable);
        long totalCategories = categoryRepository.count();
        long totalProducts = categoryRepository.sumProductsAcrossAllCategories();

        List<CategoryDTO> categories = categoryPage.getContent().stream()
                .map(result -> {
                    Category category = (Category) result[0];
                    Long productCount = (Long) result[1];
                    return new CategoryDTO(category.getId(), category.getName(), productCount.intValue());
                })
                .collect(Collectors.toList());

        Map<String, Object> response = Map.of(
                "categories", categories,
                "currentPage", pageable.getPageNumber(),
                "pageSize", pageable.getPageSize(),
                "totalCategories", totalCategories,
                "totalProducts", totalProducts,
                "totalPages", categoryPage.getTotalPages(),
                "totalElementsInPage", categoryPage.getNumberOfElements()
        );

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('" + Permissions.CATEGORY_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<String> delete(Long id) {
        if (!SecurityUtils.hasPermission(Permissions.CATEGORY_MANAGE)) {
            throw new PermissionDeniedException("You don't have permission to delete categories");
        }

        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category doesn't exist.");
        }

        try {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok("Category deleted successfully.");
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete category because products exist under this category.");
        }
    }


    public Iterable<Category> findAllByIds(Set<Long> ids) {
        return categoryRepository.findAllById(ids);
    }

    private CategoryDTO convertToDTO(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    private Category convertToEntity(CategoryDTO categoryDTO) {
        return Category.builder()
                .name(categoryDTO.getName())
                .build();
    }

    public Set<Long> existsById(Set<Long> categoryIds) {
        Set<Long> wrongIds = new HashSet<>();
        for (Long id : categoryIds) {
            if (!categoryRepository.existsById(id)) {
                wrongIds.add(id);
            }
        }
        return wrongIds;
    }


}
