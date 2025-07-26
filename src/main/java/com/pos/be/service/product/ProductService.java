package com.pos.be.service.product;

import com.pos.be.dto.product.ProductDTO;
import com.pos.be.dto.product.ProductImageDTO;
import com.pos.be.entity.category.Category;
import com.pos.be.entity.product.Product;
import com.pos.be.entity.product.ProductImage;
import com.pos.be.exception.PermissionDeniedException;
import com.pos.be.repository.category.CategoryRepository;
import com.pos.be.repository.order.ConsignmentRepository;
import com.pos.be.repository.product.ProductRepository;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.security.rbac.SecurityUtils;
import com.pos.be.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final ConsignmentRepository consignmentRepository;

    @Value("${file.upload.absolute-path}")
    private String absoluteUploadPath;
    @Value("${file.upload.url-path}")
    private String uploadDir;

    @PreAuthorize("hasAuthority('" + Permissions.CREATE_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> saveWithImages(ProductDTO dto, MultipartFile[] images) {
        if (!SecurityUtils.hasPermission(Permissions.CREATE_PRODUCT)) {
            throw new PermissionDeniedException("You don't have permission to view categories");
        }
        try {
            if (dto.getId() != null) {
                return ResponseEntity.badRequest().body("New product should not have an id.");
            }

            if (dto.getName() == null || productRepository.findByNameIgnoreCase(dto.getName()).isPresent()) {
                return ResponseEntity.badRequest().body("Product with name: '" + dto.getName() + "' already exists.");
            }

            Product product = Product.builder()
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .price(dto.getPrice())
                    .quantity(dto.getQuantity())
                    .unit(dto.getUnit())
                    .build();

            Iterable<Category> iterableCategories = categoryService.findAllByIds(dto.getCategoryIds());
            Set<Category> categories = new HashSet<>();
            iterableCategories.forEach(categories::add);
            product.setCategories(categories);

            product.setImages(new ArrayList<>());

            if (images != null && images.length > 0) {
                for (MultipartFile file : images) {
                    try {
                        String origFilename = file.getOriginalFilename();
                        String imageUrl = saveFile(file);
                        if (imageUrl == null || imageUrl.trim().isEmpty()) {
                            continue;
                        }
                        Optional<ProductImage> existingOpt = product.getImages().stream()
                                .filter(img -> {
                                    if (img.getOriginalFilename() != null && origFilename != null) {
                                        return img.getOriginalFilename().equals(origFilename);
                                    }
                                    return img.getUrl().equals(imageUrl);
                                })
                                .findFirst();
                        if (!existingOpt.isPresent()) {
                            ProductImage image = ProductImage.builder()
                                    .url(imageUrl)
                                    .originalFilename(origFilename)
                                    .primaryImage(false)
                                    .product(product)
                                    .build();
                            product.getImages().add(image);
                        }
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error saving file: " + file.getOriginalFilename());
                    }
                }
            }

            if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                for (ProductImageDTO imgDto : dto.getImages()) {
                    if (imgDto.getUrl() == null || imgDto.getUrl().trim().isEmpty()) {
                        continue;
                    }
                    Optional<ProductImage> existingOpt = product.getImages().stream()
                            .filter(img -> img.getUrl().equals(imgDto.getUrl()))
                            .findFirst();
                    if (!existingOpt.isPresent()) {
                        ProductImage image = ProductImage.builder()
                                .url(imgDto.getUrl())
                                .originalFilename(imgDto.getOriginalFilename())
                                .primaryImage(false)
                                .product(product)
                                .build();
                        product.getImages().add(image);
                    }
                }
            }

            boolean primarySet = false;
            if (dto.getImages() != null) {
                for (ProductImageDTO imgDto : dto.getImages()) {
                    if (!imgDto.isPrimaryImage()) continue;
                    Optional<ProductImage> matchingImage = product.getImages().stream()
                            .filter(img -> {
                                if (imgDto.getUrl() != null && !imgDto.getUrl().isBlank()) {
                                    return img.getUrl().equals(imgDto.getUrl());
                                } else if (imgDto.getOriginalFilename() != null && !imgDto.getOriginalFilename().isBlank()) {
                                    return img.getOriginalFilename().equals(imgDto.getOriginalFilename());
                                }
                                return false;
                            })
                            .findFirst();
                    if (matchingImage.isPresent()) {
                        product.getImages().forEach(img -> img.setPrimaryImage(false));
                        matchingImage.get().setPrimaryImage(true);
                        primarySet = true;
                        break;
                    }
                }
            }
            if (!primarySet && !product.getImages().isEmpty()) {
                product.getImages().forEach(img -> img.setPrimaryImage(false));
                product.getImages().get(0).setPrimaryImage(true);
            }
            enforceSinglePrimary(product.getImages());

            Product savedProduct = productRepository.save(product);
            return ResponseEntity.ok(convertToDTO(savedProduct));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while saving product: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> updateWithImages(ProductDTO dto, MultipartFile[] images) {
        if (!SecurityUtils.hasPermission(Permissions.UPDATE_PRODUCT)) {
            throw new PermissionDeniedException("You don't have permission to update products");
        }
        try {

            Optional<Product> productOpt = productRepository.findById(dto.getId());
            if (!productOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Product with id " + dto.getId() + " doesn't exist.");
            }
            Product product = productOpt.get();

            if (!product.getName().equalsIgnoreCase(dto.getName())) {
                return ResponseEntity.badRequest().body("Product name cannot be updated.");
            }

            Iterable<Category> iterableCategories = categoryService.findAllByIds(dto.getCategoryIds());
            Set<Category> categories = new HashSet<>();
            iterableCategories.forEach(categories::add);
            product.setCategories(categories);
            product.setDescription(dto.getDescription());
            product.setPrice(dto.getPrice());
            product.setQuantity(dto.getQuantity());
            product.setUnit(dto.getUnit());

            if (dto.getImages() != null) {
                Set<String> dtoImageUrls = dto.getImages().stream()
                        .map(ProductImageDTO::getUrl)
                        .filter(url -> url != null && !url.trim().isEmpty())
                        .collect(Collectors.toSet());
                List<ProductImage> imagesToRemove = product.getImages().stream()
                        .filter(img -> !dtoImageUrls.contains(img.getUrl()))
                        .collect(Collectors.toList());
                for (ProductImage img : imagesToRemove) {
                    product.getImages().remove(img);
                    if (isValidUploadedUrl(img.getUrl())) {
                        deleteFile(img.getUrl());
                    }
                }
            }

            if (images != null && images.length > 0) {
                for (MultipartFile file : images) {
                    try {
                        String origFilename = file.getOriginalFilename();
                        String imageUrl = saveFile(file);
                        if (imageUrl == null || imageUrl.trim().isEmpty()) continue;
                        Optional<ProductImage> existingOpt = product.getImages().stream()
                                .filter(img -> {
                                    if (img.getOriginalFilename() != null) {
                                        return img.getOriginalFilename().equals(origFilename);
                                    }
                                    return img.getUrl().equals(imageUrl);
                                })
                                .findFirst();
                        if (!existingOpt.isPresent()) {
                            ProductImage image = ProductImage.builder()
                                    .url(imageUrl)
                                    .originalFilename(origFilename)
                                    .primaryImage(false)
                                    .product(product)
                                    .build();
                            product.getImages().add(image);
                        }
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error saving file: " + file.getOriginalFilename());
                    }
                }
            }

            if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                for (ProductImageDTO imgDto : dto.getImages()) {
                    if (imgDto.getUrl() == null || imgDto.getUrl().trim().isEmpty()) continue;
                    Optional<ProductImage> existingOpt = product.getImages().stream()
                            .filter(img -> img.getUrl().equals(imgDto.getUrl()))
                            .findFirst();
                    if (!existingOpt.isPresent()) {
                        product.getImages().add(ProductImage.builder()
                                .url(imgDto.getUrl())
                                .originalFilename(imgDto.getOriginalFilename())
                                .primaryImage(false)
                                .product(product)
                                .build());
                    }
                }
            }

            boolean primarySet = false;
            if (dto.getImages() != null) {
                for (ProductImageDTO imgDTO : dto.getImages()) {
                    if (!imgDTO.isPrimaryImage()) continue;
                    Optional<ProductImage> matchingImage = product.getImages().stream()
                            .filter(img -> {
                                if (imgDTO.getUrl() != null && !imgDTO.getUrl().isBlank()) {
                                    return img.getUrl().equals(imgDTO.getUrl());
                                } else if (imgDTO.getOriginalFilename() != null && !imgDTO.getOriginalFilename().isBlank()) {
                                    return img.getOriginalFilename().equals(imgDTO.getOriginalFilename());
                                }
                                return false;
                            })
                            .findFirst();
                    if (matchingImage.isPresent()) {
                        product.getImages().forEach(img -> img.setPrimaryImage(false));
                        matchingImage.get().setPrimaryImage(true);
                        primarySet = true;
                        break;
                    }
                }
            }
            if (!primarySet && !product.getImages().isEmpty()) {
                product.getImages().forEach(img -> img.setPrimaryImage(false));
                product.getImages().get(0).setPrimaryImage(true);
            }
            enforceSinglePrimary(product.getImages());

            Product updatedProduct = productRepository.save(product);
            return ResponseEntity.ok(convertToDTO(updatedProduct));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while updating product: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('" + Permissions.READ_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> getProductsByCategory(Long categoryId, String query, Pageable pageable) {
        if (!SecurityUtils.hasPermission(Permissions.READ_PRODUCT)) {
            throw new PermissionDeniedException("You don't have permission to view products");
        }
        try {
            Page<Product> page;

            if (query != null && !query.trim().isEmpty()) {
                page = productRepository.findByCategories_IdAndNameContainingIgnoreCase(categoryId, query, pageable);
            } else {
                page = productRepository.findByCategories_Id(categoryId, pageable);
            }

            List<ProductDTO> dtos = page.getContent()
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new PageImpl<>(dtos, pageable, page.getTotalElements()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while fetching product: " + ex.getMessage());
        }
    }

    private void enforceSinglePrimary(List<ProductImage> images) {
        boolean primaryFound = false;
        for (ProductImage img : images) {
            if (img.isPrimaryImage()) {
                if (!primaryFound) {
                    primaryFound = true;
                } else {
                    img.setPrimaryImage(false);
                }
            }
        }
        if (!primaryFound && !images.isEmpty()) {
            images.get(0).setPrimaryImage(true);
        }
    }

    @PreAuthorize("hasAuthority('" + Permissions.READ_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> get(Long id) {
        if (!SecurityUtils.hasPermission(Permissions.READ_PRODUCT)) {
            throw new PermissionDeniedException("You don't have permission to view products");
        }
        try {
            Optional<Product> productOpt = productRepository.findById(id);
            if (productOpt.isPresent()) {
                return ResponseEntity.ok(convertToDTO(productOpt.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product with id " + id + " doesn't exist.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while fetching product: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('" + Permissions.READ_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Page<ProductDTO> getProducts(String name, Pageable pageable) {
        if (!SecurityUtils.hasPermission(Permissions.READ_PRODUCT)) {
            throw new PermissionDeniedException("You don't have permission to view products");
        }
        Page<Product> productsPage;
        if (name != null && !name.trim().isEmpty()) {
            productsPage = productRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            productsPage = productRepository.findAll(pageable);
        }
        List<ProductDTO> dtos = productsPage.getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, productsPage.getTotalElements());
    }

    @PreAuthorize("hasAuthority('" + Permissions.READ_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> getDetailedProduct(Long id) {
        if (!SecurityUtils.hasPermission(Permissions.READ_PRODUCT)) {
            throw new PermissionDeniedException("You don't have permission to view product details");
        }
        try {
            Object details = productRepository.getProductDetailsById(id);
            if (details == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Product with id " + id + " not found.");
            }
            return ResponseEntity.ok(details);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while fetching detailed product: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('" + Permissions.DELETE_PRODUCT + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> delete(Long id) {
        if (!SecurityUtils.hasPermission(Permissions.DELETE_PRODUCT)) {
            throw new PermissionDeniedException("You don't have permission to delete products");
        }
        try {
            Optional<Product> productOpt = productRepository.findById(id);
            if (!productOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Product with id " + id + " doesn't exist.");
            }
            if (consignmentRepository.existsConsignmentByProductId(id)) {
                throw new DataIntegrityViolationException("This product has pending orders.");
            }
            Product product = productOpt.get();
            if (product.getImages() != null) {
                for (ProductImage image : product.getImages()) {
                    deleteFile(image.getUrl());
                }
            }
            productRepository.deleteById(id);
            return ResponseEntity.ok(id);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Cannot delete product: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error while deleting product: " + ex.getMessage());
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        if (absoluteUploadPath == null || absoluteUploadPath.isBlank()) {
            throw new IOException("Upload directory is not configured properly");
        }
        File uploadPath = new File(absoluteUploadPath);
        if (!uploadPath.exists() && !uploadPath.mkdirs()) {
            throw new IOException("Could not create directory: " + absoluteUploadPath);
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IOException("Invalid file name");
        }
        String cleanFilename = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        String fileName = System.currentTimeMillis() + "_" + cleanFilename;
        File dest = new File(uploadPath, fileName);
        file.transferTo(dest);
        return uploadDir + fileName;
    }

    private void deleteFile(String fileUrl) {
        try {
            String filePath = fileUrl.replaceFirst(uploadDir, absoluteUploadPath)
                    .replace("/", File.separator);
            File file = new File(filePath);
            if (file.exists() && !file.delete()) {
                System.err.println("Failed to delete file: " + filePath);
            }
        } catch (SecurityException e) {
            System.err.println("Security exception deleting file: " + e.getMessage());
        }
    }

    private boolean isValidImageUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return isValidWebUrl(url);
        }
        return url.startsWith("/uploads/products/")
                && !url.contains("..")
                && url.length() > "/uploads/products/".length();
    }

    private boolean isValidWebUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private boolean isValidUploadedUrl(String url) {
        return url.startsWith("/uploads/products/")
                && !url.contains("..")
                && !url.isBlank()
                && url.length() > "/uploads/products/".length();
    }

    private ProductDTO convertToDTO(Product product) {
        Set<Long> categoryIds = product.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        List<ProductImageDTO> imageDTOs = new ArrayList<>();
        if (product.getImages() != null) {
            imageDTOs = product.getImages().stream()
                    .map(img -> ProductImageDTO.builder()
                            .id(img.getId())
                            .url(img.getUrl())
                            .originalFilename(img.getOriginalFilename())
                            .primaryImage(img.isPrimaryImage())
                            .build())
                    .collect(Collectors.toList());
        }
        return ProductDTO.builder()
                .id(product.getProduct_id())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .unit(product.getUnit())
                .categoryIds(categoryIds)
                .images(imageDTOs)
                .build();
    }

    public List<ProductDTO> findAllById(List<Long> ids) {
        List<Product> products = StreamSupport
                .stream(productRepository.findAllById(ids).spliterator(), false)
                .collect(Collectors.toList());

        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
