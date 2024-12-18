package com.pos.be.service.product;

import com.pos.be.dto.product.CustomOptionsDTO;
import com.pos.be.entity.product.CustomOptions;
import com.pos.be.entity.product.Product;
import com.pos.be.repository.product.CustomOptionsRepository;
import com.pos.be.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomOptionsService {

    private final CustomOptionsRepository customOptionsRepository;
    private final ProductRepository productRepository;

    @Autowired
    CustomOptionsService(CustomOptionsRepository customOptionsRepository, ProductRepository productRepository) {
        this.customOptionsRepository = customOptionsRepository;
        this.productRepository = productRepository;
    }

    public ResponseEntity<?> save(CustomOptionsDTO customOptionsDTO) {
        customOptionsRepository.save(convertToEntity(customOptionsDTO));
        return ResponseEntity.ok("Custom Option Saved Successfully.");
    }

    public ResponseEntity<?> get(Long id) {
        Optional<CustomOptions> customOptions = customOptionsRepository.findById(id);
        if (customOptions.isPresent()) {
            return ResponseEntity.ok(convertToDTO(customOptions.get()));
        }
        return ResponseEntity.ok("Custom Option With Id: " + id + " Doesn't Exists.");
    }

    public ResponseEntity<?> getAll() {
        List<CustomOptionsDTO> customOptionsDTOS = new ArrayList<>();
        customOptionsRepository.findAll().forEach(
                customOption -> customOptionsDTOS.add(convertToDTO(customOption))
        );
        return ResponseEntity.ok(customOptionsDTOS);
    }

    public ResponseEntity<?> update(CustomOptionsDTO dto) {
        Optional<CustomOptions> customOptionsUpdate = customOptionsRepository.findById(dto.getId()).map(
                customOptions -> {
                    customOptions.setOptionLabel(dto.getOptionLabel());
                    customOptions.setOptionType(dto.getOptionType());
                    customOptions.setOptionValue(dto.getOptionValue());
                    return customOptions;
                }
        );
        if (customOptionsUpdate.isPresent()) {
            customOptionsRepository.save(customOptionsUpdate.get());
            return ResponseEntity.ok("Custom Option Update.");
        } else {
            return ResponseEntity.ok("Custom Option With Id: " + dto.getId() + " Doesn't Exists.");
        }
    }

    public ResponseEntity<?> delete(Long id) {
        if (customOptionsRepository.existsById(id)) {
            customOptionsRepository.deleteById(id);
            return ResponseEntity.ok("Custom Option With Id: " + id + " Deleted Successfully");
        } else {
            return ResponseEntity.ok("Custom Option With Id: " + id + " Doesn't Exists.");
        }
    }

    private CustomOptionsDTO convertToDTO(CustomOptions customOptions) {
        return CustomOptionsDTO.builder()
                .id(customOptions.getId())
                .optionLabel(customOptions.getOptionLabel())
                .optionType(customOptions.getOptionType())
                .optionValue(customOptions.getOptionValue())
                .productId(customOptions.getProduct().getId())
                .build();
    }

    private CustomOptions convertToEntity(CustomOptionsDTO customOptionsDTO) {
        Product product = productRepository.findById(customOptionsDTO.getProductId()).get();
        return CustomOptions.builder()
                .optionLabel(customOptionsDTO.getOptionLabel())
                .optionType(customOptionsDTO.getOptionType())
                .optionValue(customOptionsDTO.getOptionValue())
                .product(product)
                .build();
    }
}
