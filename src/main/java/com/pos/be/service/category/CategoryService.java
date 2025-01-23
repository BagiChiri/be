package com.pos.be.service.category;

import com.pos.be.dto.category.CategoryDTO;
import com.pos.be.entity.category.Category;
import com.pos.be.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public ResponseEntity<?> save(
            CategoryDTO request
    ) {
        var category = convertToEntity(request);
        try {
            var categoryDTO = convertToDTO(categoryRepository.save(category));
            return ResponseEntity.ok(categoryDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to add category.");
        }
    }

    public ResponseEntity<?> update(CategoryDTO request) {
        Category category = categoryRepository.findById(
                        request.getId()
                )
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category doesn't exist.")
                );
        category.setId(request.getId());
        category.setName(request.getName());
        categoryRepository.save(category);
        return ResponseEntity.ok().body("Updated Successfully.");
    }

    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(
                convertToDTO(categoryRepository.findAll())
        );
    }

    public ResponseEntity<?> get(Long id) {
        Category category = categoryRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category doesn't exists")
                );
        CategoryDTO categoryDTO = convertToDTO(category);

        return ResponseEntity.ok(categoryDTO);
    }

    public ResponseEntity<?> delete(Long id) {
        if (categoryRepository.existsById(id)) {
            try {
                categoryRepository.deleteById(id);
                return ResponseEntity.ok(id);
            } catch (DataIntegrityViolationException e) {
                // Handle foreign key constraint violation
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Cannot delete category because products exist under this category.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Category doesn't exist.");
        }
    }


    public Iterable<Category> findAllByIds(Set<Long> ids) {
        return categoryRepository.findAllById(ids);
    }

    private List<CategoryDTO> convertToDTO(Iterable<Category> iterable) {
        if (iterable == null) {
            throw new IllegalArgumentException("Iterable cannot be null.");
        }
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        iterable.forEach(category ->
                categoryDTOList.add(
                        CategoryDTO.builder()
                                .id(category.getId())
                                .name(category.getName())
                                .build()
                )
        );
        return categoryDTOList;
    }

    private CategoryDTO convertToDTO(Category single) {
        if (single == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        return CategoryDTO.builder()
                .id(single.getId())
                .name(single.getName())
                .build();
    }




    private Category convertToEntity(CategoryDTO categoryDTO) {
        return Category.builder()
                .name(categoryDTO.getName())
                .build();
    }

    public Set<Long> existsById(Set<Long> categoryIds) {
        Set<Long> wrongIds = new HashSet<>();
        for (Long id :
                categoryIds) {
            if (!categoryRepository.existsById(id)) {
                wrongIds.add(id);
            }
        }
        return wrongIds;
    }
}
