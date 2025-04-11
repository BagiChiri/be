//package com.pos.be.service.product;
//
//import com.pos.be.dto.product.ProductDTO;
//import com.pos.be.dto.product.ProductImageDTO;
//import com.pos.be.entity.category.Category;
//import com.pos.be.entity.product.Product;
//import com.pos.be.entity.product.ProductImage;
//import com.pos.be.repository.category.CategoryRepository;
//import com.pos.be.repository.order.ConsignmentRepository;
//import com.pos.be.repository.product.ProductRepository;
//import com.pos.be.security.rbac.Permissions;
//import com.pos.be.security.rbac.SecurityUtils;
//import com.pos.be.service.category.CategoryService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import com.pos.be.exception.PermissionDeniedException;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.*;
//import java.util.stream.Collectors;
//
///*
//* Data Filtering (for multi-tenant or department-specific access):
//
//java
//Copy
//@PreAuthorize("hasPermission(#productId, 'Product', 'read')")
//public ProductDTO getProduct(Long productId) {
//    // ...
//}*/
//
//@Service
//@RequiredArgsConstructor
//public class ProductService {
//
//    private final ProductRepository productRepository;
//    private final CategoryService categoryService;
//    private final CategoryRepository categoryRepository;
//    private final ConsignmentRepository consignmentRepository;
//
//    @Value("${file.upload.absolute-path}")
//    private String absoluteUploadPath;// = "C:" + File.separator + "uploads" + File.separator + "products" + File.separator;
//    @Value("${file.upload.url-path}")
//    private String uploadDir;// = "/uploads/products/";
//
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> saveWithImages(ProductDTO productDTO, MultipartFile[] images) {
//        if (!SecurityUtils.hasPermission(Permissions.PRODUCT_MANAGE)) {
//            throw new PermissionDeniedException("You don't have permission to create products");
//        }
//
//        if (productRepository.findByNameIgnoreCase(productDTO.getName()).isPresent()) {
//            return ResponseEntity.badRequest().body("Product with name '" + productDTO.getName() + "' already exists.");
//        }
//
//        Set<Long> wrongIds = categoryService.existsById(productDTO.getCategoryIds());
//        if (!wrongIds.isEmpty()) {
//            return ResponseEntity.badRequest().body("Category id(s): " + wrongIds + " do not exist.");
//        }
//
//        Set<String> uniqueUrls = new HashSet<>();
//        Set<String> uniqueImages = new HashSet<>();
//        List<ProductImageDTO> imageDTOs = new ArrayList<>();
//        ProductImageDTO primaryImageDTO = null;
//
//        List<ProductImageDTO> providedImages = productDTO.getImages();
//        if (providedImages != null) {
//            for (ProductImageDTO imgDTO : providedImages) {
//                String url = imgDTO.getUrl();
//                if (isValidImageUrl(url) && uniqueUrls.add(url)) {
//                    ProductImageDTO newImageDTO = ProductImageDTO.builder()
//                            .url(url)
//                            .primaryImage(false)
//                            .build();
//                    imageDTOs.add(newImageDTO);
//                    if (imgDTO.isPrimaryImage() && primaryImageDTO == null) {
//                        primaryImageDTO = newImageDTO;
//                    }
//                } else {
//                    System.out.println("invalid url: " + url);
//                }
//            }
//        }
//
//        if (images != null) {
//            for (MultipartFile file : images) {
//                try {
//                    if (file.isEmpty()) continue;
//
//                    String imageUrl = saveFile(file);
//                    if (isValidUploadedUrl(imageUrl)) {
//                        if (uniqueImages.add(imageUrl)) {
//                            ProductImageDTO newImageDTO = ProductImageDTO.builder()
//                                    .url(imageUrl)
//                                    .primaryImage(false)
//                                    .build();
//                            imageDTOs.add(newImageDTO);
//                            if (primaryImageDTO == null) {
//                                primaryImageDTO = newImageDTO;
//                            }
//                        }
//                    } else {
//                        deleteFile(imageUrl); // Clean up invalid file
//                        return ResponseEntity.badRequest()
//                                .body("Invalid image path generated: " + imageUrl);
//                    }
//                } catch (IOException e) {
//                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                            .body("Error saving file: " + file.getOriginalFilename());
//                }
//            }
//        }
//
//
//        if (primaryImageDTO != null) {
//            primaryImageDTO.setPrimaryImage(true);
//        } else if (!imageDTOs.isEmpty()) {
//            imageDTOs.get(0).setPrimaryImage(true);
//        }
//
//        productDTO.setImages(imageDTOs);
//        Product product = convertToEntity(productDTO);
//        Product savedProduct = productRepository.save(product);
//        return ResponseEntity.ok(convertToDTO(savedProduct));
//    }
//
//    private boolean isValidImageUrl(String url) {
//        if (url == null || url.isBlank()) {
//            return false;
//        }
//
//        if (url.startsWith("http://") || url.startsWith("https://")) {
//            return isValidWebUrl(url);
//        }
//
//        return url.startsWith("/uploads/products/")
//                && !url.contains("..")
//                && url.length() > "/uploads/products/".length();
//    }
//
//    private boolean isValidWebUrl(String url) {
//        try {
//            new URI(url);
//            return true;
//        } catch (URISyntaxException e) {
//            return false;
//        }
//    }
//
//    private boolean isValidUploadedUrl(String url) {
//        return url.startsWith("/uploads/products/")
//                && !url.contains("..")
//                && !url.isBlank()
//                && !url.isEmpty()
//                && url.length() > "/uploads/products/".length();
//    }
//
//    //    public ResponseEntity<?> updateWithImages(ProductDTO dto, MultipartFile[] images) {
////        Optional<Product> productOpt = productRepository.findById(dto.getId());
////        if (!productOpt.isPresent()) {
////            return ResponseEntity.status(HttpStatus.NOT_FOUND)
////                    .body("Product with id " + dto.getId() + " doesn't exist.");
////        }
////        Product product = productOpt.get();
////
////        if (!product.getName().equalsIgnoreCase(dto.getName())) {
////            return ResponseEntity.badRequest().body("Product name cannot be updated.");
////        }
////
////        Iterable<Category> iterableCategories = categoryService.findAllByIds(dto.getCategoryIds());
////        Set<Category> categories = new HashSet<>();
////        iterableCategories.forEach(categories::add);
////        product.setCategories(categories);
////
////        product.setDescription(dto.getDescription());
////        product.setPrice(dto.getPrice());
////        product.setQuantity(dto.getQuantity());
////        product.setUnit(dto.getUnit());
////
////        if (product.getImages() == null) {
////            product.setImages(new ArrayList<>());
////        }
////
////        if (images != null && images.length > 0) {
////            List<ProductImage> newImageList = new ArrayList<>();
////            Set<String> newImageUrls = new HashSet<>();
////            for (int i = 0; i < images.length; i++) {
////                MultipartFile file = images[i];
////                try {
////                    String imageUrl = saveFile(file);
////                    boolean duplicate = product.getImages().stream().anyMatch(img -> img.getUrl().equals(imageUrl))
////                            || newImageUrls.contains(imageUrl);
////                    if (!duplicate) {
////                        newImageUrls.add(imageUrl);
////                        ProductImage image = ProductImage.builder()
////                                .url(imageUrl)
////                                .primaryImage(i == 0)
////                                .product(product)
////                                .build();
////                        newImageList.add(image);
////                    }
////                } catch (IOException e) {
////                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
////                            .body("Error saving file: " + file.getOriginalFilename());
////                }
////            }
////            // Merge new file images with existing ones.
////            product.getImages().addAll(newImageList);
////        }
////        else if (dto.getImages() != null && !dto.getImages().isEmpty()) {
////            for (ProductImageDTO imgDto : dto.getImages()) {
////                Optional<ProductImage> existingOpt = product.getImages().stream()
////                        .filter(img -> img.getUrl().equals(imgDto.getUrl()))
////                        .findFirst();
////                if (existingOpt.isPresent()) {
////                    existingOpt.get().setPrimaryImage(imgDto.isPrimaryImage());
////                } else {
////                    product.getImages().add(ProductImage.builder()
////                            .url(imgDto.getUrl())
////                            .primaryImage(imgDto.isPrimaryImage())
////                            .product(product)
////                            .build());
////                }
////            }
////        }
////
////        enforceSinglePrimary(product.getImages());
////
////        Product updatedProduct = productRepository.save(product);
////        return ResponseEntity.ok(convertToDTO(updatedProduct));
////    }
////    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
////    public ResponseEntity<?> updateWithImages(ProductDTO dto, MultipartFile[] images) {
////        // Additional service-level permission check
////        if (!SecurityUtils.hasPermission(Permissions.PRODUCT_MANAGE)) {
////            throw new PermissionDeniedException("You don't have permission to update products");
////        }
////
////        Optional<Product> productOpt = productRepository.findById(dto.getId());
////        if (!productOpt.isPresent()) {
////            return ResponseEntity.status(HttpStatus.NOT_FOUND)
////                    .body("Product with id " + dto.getId() + " doesn't exist.");
////        }
////        Product product = productOpt.get();
////
////        // Enforce that product name remains immutable.
////        if (!product.getName().equalsIgnoreCase(dto.getName())) {
////            return ResponseEntity.badRequest().body("Product name cannot be updated.");
////        }
////
////        // Update categories.
////        Iterable<Category> iterableCategories = categoryService.findAllByIds(dto.getCategoryIds());
////        Set<Category> categories = new HashSet<>();
////        iterableCategories.forEach(categories::add);
////        product.setCategories(categories);
////
////        product.setDescription(dto.getDescription());
////        product.setPrice(dto.getPrice());
////        product.setQuantity(dto.getQuantity());
////        product.setUnit(dto.getUnit());
////
////        // Ensure product images collection is not null.
////        if (product.getImages() == null) {
////            product.setImages(new ArrayList<>());
////        }
////
////        // === STEP 1: Remove images that have been removed in the frontend ===
////        if (dto.getImages() != null) {
////            // Build a set of non-empty image URLs from the incoming DTO.
////            Set<String> dtoImageUrls = dto.getImages().stream()
////                    .map(ProductImageDTO::getUrl)
////                    .filter(url -> url != null && !url.trim().isEmpty())
////                    .collect(Collectors.toSet());
////
////            // Identify images to remove (those that are in the product but not in the DTO)
////            List<ProductImage> imagesToRemove = product.getImages().stream()
////                    .filter(img -> !dtoImageUrls.contains(img.getUrl()))
////                    .collect(Collectors.toList());
////
////            // Remove these images and delete files if necessary.
////            for (ProductImage img : imagesToRemove) {
////                product.getImages().remove(img);
////                if (isValidUploadedUrl(img.getUrl())) {
////                    deleteFile(img.getUrl());
////                }
////            }
////        }
////        boolean hasPrimaryFromDto = dto.getImages() != null && dto.getImages().stream()
////                .anyMatch(ProductImageDTO::isPrimaryImage);
////        // === STEP 2: Process file uploads if provided ===
////        if (images != null && images.length > 0) {
////            List<ProductImage> newImageList = new ArrayList<>();
////            // Track new image URLs in case of duplicates.
////            Set<String> newImageUrls = new HashSet<>();
////
////            for (MultipartFile file : images) {
////                try {
////                    String imageUrl = saveFile(file);
////                    // Ensure the returned URL is not blank.
////                    if (imageUrl == null || imageUrl.trim().isEmpty()) {
////                        continue; // Skip this file if no valid URL is returned.
////                    }
////
////                    // Check if this image URL exists in the DTO to get its primary flag
////                    boolean isPrimary = false;
////                    if (dto.getImages() != null) {
////                        isPrimary = dto.getImages().stream()
////                                .filter(img -> img.getUrl() != null && img.getUrl().equals(imageUrl))
////                                .findFirst()
////                                .map(ProductImageDTO::isPrimaryImage)
////                                .orElse(false);
////                    }
////
////                    // If no primary from DTO and this is the first new image, mark as primary
////                    if (!hasPrimaryFromDto && newImageList.isEmpty()) {
////                        isPrimary = true;
////                    }
////
////                    // Check if an image with this URL already exists.
////                    Optional<ProductImage> existingOpt = product.getImages().stream()
////                            .filter(img -> img.getUrl().equals(imageUrl))
////                            .findFirst();
////
////                    if (existingOpt.isPresent()) {
////                        // Update the primary flag if specified in DTO
////                        existingOpt.get().setPrimaryImage(isPrimary);
////                    } else if (!newImageUrls.contains(imageUrl)) {
////                        newImageUrls.add(imageUrl);
////                        ProductImage image = ProductImage.builder()
////                                .url(imageUrl)
////                                .primaryImage(isPrimary)
////                                .product(product)
////                                .build();
////                        newImageList.add(image);
////                    }
////                } catch (IOException e) {
////                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
////                            .body("Error saving file: " + file.getOriginalFilename());
////                }
////            }
////            product.getImages().addAll(newImageList);
////        }
////
////        // === STEP 3: Process DTO-provided image URLs ===
////        // This covers images that are provided as URLs (not via file uploads).
////        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
////            dto.getImages().stream()
////                    .filter(imgDto -> imgDto.getUrl() != null && !imgDto.getUrl().trim().isEmpty())
////                    .forEach(imgDto -> {
////                        Optional<ProductImage> existingOpt = product.getImages().stream()
////                                .filter(img -> img.getUrl().equals(imgDto.getUrl()))
////                                .findFirst();
////                        if (existingOpt.isPresent()) {
////                            // Update the primary flag.
////                            existingOpt.get().setPrimaryImage(imgDto.isPrimaryImage());
////                        } else {
////                            // Add the new image.
////                            product.getImages().add(ProductImage.builder()
////                                    .url(imgDto.getUrl())
////                                    .primaryImage(imgDto.isPrimaryImage())
////                                    .product(product)
////                                    .build());
////                        }
////                    });
////        }
////
////        // Enforce that only one image is marked as primary.
////        enforceSinglePrimary(product.getImages());
////
////        Product updatedProduct = productRepository.save(product);
////        return ResponseEntity.ok(convertToDTO(updatedProduct));
////    }
//
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> updateWithImages(ProductDTO dto, MultipartFile[] images) {
//        // Additional service-level permission check
//        if (!SecurityUtils.hasPermission(Permissions.PRODUCT_MANAGE)) {
//            throw new PermissionDeniedException("You don't have permission to update products");
//        }
//
//        Optional<Product> productOpt = productRepository.findById(dto.getId());
//        if (!productOpt.isPresent()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body("Product with id " + dto.getId() + " doesn't exist.");
//        }
//        Product product = productOpt.get();
//
//        // Enforce that product name remains immutable.
//        if (!product.getName().equalsIgnoreCase(dto.getName())) {
//            return ResponseEntity.badRequest().body("Product name cannot be updated.");
//        }
//
//        // Update categories.
//        Iterable<Category> iterableCategories = categoryService.findAllByIds(dto.getCategoryIds());
//        Set<Category> categories = new HashSet<>();
//        iterableCategories.forEach(categories::add);
//        product.setCategories(categories);
//
//        product.setDescription(dto.getDescription());
//        product.setPrice(dto.getPrice());
//        product.setQuantity(dto.getQuantity());
//        product.setUnit(dto.getUnit());
//
//        if (dto.getImages() != null) {
//            Set<String> dtoImageUrls = dto.getImages().stream()
//                    .map(ProductImageDTO::getUrl)
//                    .filter(url -> url != null && !url.trim().isEmpty())
//                    .collect(Collectors.toSet());
//
//            List<ProductImage> imagesToRemove = product.getImages().stream()
//                    .filter(img -> !dtoImageUrls.contains(img.getUrl()))
//                    .collect(Collectors.toList());
//
//            for (ProductImage img : imagesToRemove) {
//                product.getImages().remove(img);
//                if (isValidUploadedUrl(img.getUrl())) {
//                    deleteFile(img.getUrl());
//                }
//            }
//        }
//
//        // === STEP 2: Process file uploads if provided ===
//        if (images != null && images.length > 0) {
//            Set<String> newImageUrls = new HashSet<>();
//            List<ProductImage> newImageList = new ArrayList<>();
//
//            for (MultipartFile file : images) {
//                try {
//                    String imageUrl = saveFile(file);
//                    if (imageUrl == null || imageUrl.trim().isEmpty()) continue;
//
//                    Optional<ProductImage> existingOpt = product.getImages().stream()
//                            .filter(img -> img.getUrl().equals(imageUrl))
//                            .findFirst();
//
//                    if (!existingOpt.isPresent() && !newImageUrls.contains(imageUrl)) {
//                        newImageUrls.add(imageUrl);
//                        ProductImage image = ProductImage.builder()
//                                .url(imageUrl)
//                                .primaryImage(false) // temp false, will fix later
//                                .product(product)
//                                .build();
//                        newImageList.add(image);
//                    }
//                } catch (IOException e) {
//                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                            .body("Error saving file: " + file.getOriginalFilename());
//                }
//            }
//            product.getImages().addAll(newImageList);
//        }
//
//        // === STEP 3: Process DTO-provided image URLs ===
//        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
//            dto.getImages().stream()
//                    .filter(imgDto -> imgDto.getUrl() != null && !imgDto.getUrl().trim().isEmpty())
//                    .forEach(imgDto -> {
//                        Optional<ProductImage> existingOpt = product.getImages().stream()
//                                .filter(img -> img.getUrl().equals(imgDto.getUrl()))
//                                .findFirst();
//                        if (!existingOpt.isPresent()) {
//                            product.getImages().add(ProductImage.builder()
//                                    .url(imgDto.getUrl())
//                                    .primaryImage(false) // temp false, will fix later
//                                    .product(product)
//                                    .build());
//                        }
//                    });
//        }
//
//        // === FINAL STEP: Set primary based on DTO ===
//        Set<String> primaryImageUrls = dto.getImages().stream()
//                .filter(ProductImageDTO::isPrimaryImage)
//                .map(ProductImageDTO::getUrl)
//                .filter(url -> url != null && !url.trim().isEmpty())
//                .collect(Collectors.toSet());
//
//        for (ProductImage img : product.getImages()) {
//            img.setPrimaryImage(primaryImageUrls.contains(img.getUrl()));
//        }
//
//        // Enforce only one primary image
//        enforceSinglePrimary(product.getImages());
//
//        Product updatedProduct = productRepository.save(product);
//        return ResponseEntity.ok(convertToDTO(updatedProduct));
//    }
//
//    private void processImages(Product product, ProductDTO dto, MultipartFile[] images) {
//        // A. Initialize if null
//        if (product.getImages() == null) {
//            product.setImages(new ArrayList<>());
//        }
//
//        // B. Process existing images from DTO
//        Map<String, ProductImage> existingImages = product.getImages().stream()
//                .collect(Collectors.toMap(ProductImage::getUrl, img -> img));
//
//        List<ProductImage> updatedImages = new ArrayList<>();
//
//        if (dto.getImages() != null) {
//            for (ProductImageDTO imgDto : dto.getImages()) {
//                if (imgDto.getUrl() == null || imgDto.getUrl().isEmpty()) continue;
//
//                ProductImage image = existingImages.get(imgDto.getUrl());
//                if (image != null) {
//                    // Update existing image
//                    image.setPrimaryImage(imgDto.isPrimaryImage());
//                    updatedImages.add(image);
//                } else if (!imgDto.getUrl().startsWith("blob:")) {
//                    // Add new URL-based image
//                    updatedImages.add(new ProductImage(imgDto.getUrl(), imgDto.isPrimaryImage(), product));
//                }
//            }
//        }
//
//        // C. Process file uploads
//        if (images != null) {
//            for (MultipartFile file : images) {
//                try {
//                    String imageUrl = saveFile(file);
//                    if (imageUrl != null && !imageUrl.isEmpty()) {
//                        // Find matching DTO for this upload
//                        boolean isPrimary = dto.getImages().stream()
//                                .filter(img -> img.getUrl() != null && img.getUrl().contains("blob:"))
//                                .findFirst()
//                                .map(ProductImageDTO::isPrimaryImage)
//                                .orElse(false);
//
//                        updatedImages.add(new ProductImage(imageUrl, isPrimary, product));
//                    }
//                } catch (IOException e) {
//                    throw new RuntimeException("Failed to save image", e);
//                }
//            }
//        }
//
//        // D. Set updated images and enforce single primary
//        product.setImages(updatedImages);
//        enforceSinglePrimary(product.getImages());
//    }
//
//
//    private void enforceSinglePrimary(List<ProductImage> images) {
//        boolean primaryFound = false;
//        for (ProductImage img : images) {
//            if (img.isPrimaryImage()) {
//                if (!primaryFound) {
//                    primaryFound = true;
//                } else {
//                    img.setPrimaryImage(false);
//                }
//            }
//        }
//        if (!primaryFound && !images.isEmpty()) {
//            images.get(0).setPrimaryImage(true);
//        }
//    }
//
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> get(Long id) {
//        // Additional service-level permission check
//        if (!SecurityUtils.hasPermission(Permissions.PRODUCT_VIEW)) {
//            throw new PermissionDeniedException("You don't have permission to view products");
//        }
//
//        Optional<Product> productOpt = productRepository.findById(id);
//        if (productOpt.isPresent()) {
//            return ResponseEntity.ok(convertToDTO(productOpt.get()));
//        }
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body("Product with id " + id + " doesn't exist.");
//    }
//
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public Page<ProductDTO> getProducts(String name, Pageable pageable) {
//        // Additional service-level permission check
//        if (!SecurityUtils.hasPermission(Permissions.PRODUCT_VIEW)) {
//            throw new PermissionDeniedException("You don't have permission to view products");
//        }
//
//        Page<Product> productsPage;
//        if (name != null && !name.trim().isEmpty()) {
//            productsPage = productRepository.findByNameContainingIgnoreCase(name, pageable);
//        } else {
//            productsPage = productRepository.findAll(pageable);
//        }
//        List<ProductDTO> dtos = productsPage.getContent()
//                .stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//        return new PageImpl<>(dtos, pageable, productsPage.getTotalElements());
//    }
//
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public Page<ProductDTO> getProductsByCategory(Long categoryId, String query, Pageable pageable) {
//        // Additional service-level permission check
//        if (!SecurityUtils.hasPermission(Permissions.PRODUCT_VIEW)) {
//            throw new PermissionDeniedException("You don't have permission to view products");
//        }
//        Page<Product> page;
//
//        if (query != null && !query.trim().isEmpty()) {
//            page = productRepository.findByCategories_IdAndNameContainingIgnoreCase(categoryId, query, pageable);
//        } else {
//            page = productRepository.findByCategories_Id(categoryId, pageable);
//        }
//
//        List<ProductDTO> dtos = page.getContent()
//                .stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//
//        return new PageImpl<>(dtos, pageable, page.getTotalElements());
//    }
//
//
//
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> getDetailedProduct(Long id) {
//        // Additional service-level permission check
//        if (!SecurityUtils.hasPermission(Permissions.PRODUCT_VIEW)) {
//            throw new PermissionDeniedException("You don't have permission to view product details");
//        }
//
//        Object details = productRepository.getProductDetailsById(id);
//        if (details == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body("Product with id " + id + " not found.");
//        }
//        return ResponseEntity.ok(details);
//    }
//
//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> delete(Long id) {
//        // Additional service-level permission check
//        if (!SecurityUtils.hasPermission(Permissions.PRODUCT_MANAGE)) {
//            throw new PermissionDeniedException("You don't have permission to delete products");
//        }
//
//        Optional<Product> productOpt = productRepository.findById(id);
//        if (!productOpt.isPresent()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body("Product with id " + id + " doesn't exist.");
//        }
//        if (consignmentRepository.existsConsignmentByProductId(id)) {
//            throw new DataIntegrityViolationException("This product has pending orders.");
//        }
//        Product product = productOpt.get();
//        if (product.getImages() != null) {
//            for (ProductImage image : product.getImages()) {
//                deleteFile(image.getUrl());
//            }
//        }
//        productRepository.deleteById(id);
//        return ResponseEntity.ok(id);
//    }
//
//    private String saveFile(MultipartFile file) throws IOException {
//        // Validate upload directory configuration
//        if (absoluteUploadPath == null || absoluteUploadPath.isBlank()) {
//            throw new IOException("Upload directory is not configured properly");
//        }
//
//        File uploadPath = new File(absoluteUploadPath);
//        if (!uploadPath.exists() && !uploadPath.mkdirs()) {
//            throw new IOException("Could not create directory: " + absoluteUploadPath);
//        }
//
//        // Validate filename
//        String originalFilename = file.getOriginalFilename();
//        if (originalFilename == null || originalFilename.isBlank()) {
//            throw new IOException("Invalid file name");
//        }
//
//        // Sanitize filename
//        String cleanFilename = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
//        String fileName = System.currentTimeMillis() + "_" + cleanFilename;
//
//        // Create destination path
//        File dest = new File(uploadPath, fileName);
//
//        // Save file
//        file.transferTo(dest);
//
//        // Return web-accessible URL path
//        return uploadDir + fileName; // Adjust this to match your static resource mapping
//    }
//
//
//    private void deleteFile(String fileUrl) {
//        try {
//            String filePath = fileUrl.replaceFirst(uploadDir, absoluteUploadPath)
//                    .replace("/", File.separator);
//            File file = new File(filePath);
//            if (file.exists()) {
//                if (!file.delete()) {
//                    System.err.println("Failed to delete file: " + filePath);
//                }
//            }
//        } catch (SecurityException e) {
//            System.err.println("Security exception deleting file: " + e.getMessage());
//        }
//    }
//
//    private ProductDTO convertToDTO(Product product) {
//        Set<Long> categoryIds = product.getCategories().stream()
//                .map(Category::getId)
//                .collect(Collectors.toSet());
//        List<ProductImageDTO> imageDTOs = new ArrayList<>();
//        if (product.getImages() != null) {
//            imageDTOs = product.getImages().stream()
//                    .map(img -> ProductImageDTO.builder()
//                            .id(img.getId())
//                            .url(img.getUrl())
//                            .primaryImage(img.isPrimaryImage())
//                            .build())
//                    .collect(Collectors.toList());
//        }
//        return ProductDTO.builder()
//                .id(product.getProduct_id())
//                .name(product.getName())
//                .description(product.getDescription())
//                .price(product.getPrice())
//                .quantity(product.getQuantity())
//                .unit(product.getUnit())
//                .categoryIds(categoryIds)
//                .images(imageDTOs)
//                .build();
//    }
//
//    private Product convertToEntity(ProductDTO dto) {
//        Iterable<Category> iterableCategories = categoryService.findAllByIds(dto.getCategoryIds());
//        Set<Category> categorySet = new HashSet<>();
//        iterableCategories.forEach(categorySet::add);
//
//        Product product = Product.builder()
//                .name(dto.getName())
//                .description(dto.getDescription())
//                .price(dto.getPrice())
//                .quantity(dto.getQuantity())
//                .unit(dto.getUnit())
//                .categories(categorySet)
//                .build();
//
//        if (dto.getImages() != null) {
//            List<ProductImage> imageList = dto.getImages().stream()
//                    .map(imgDto -> ProductImage.builder()
//                            .url(imgDto.getUrl())
//                            .primaryImage(imgDto.isPrimaryImage())
//                            .product(product)
//                            .build())
//                    .collect(Collectors.toList());
//            product.setImages(imageList);
//        }
//        return product;
//    }
//}
package com.pos.be.service.product;

import com.pos.be.dto.product.ProductDTO;
import com.pos.be.dto.product.ProductImageDTO;
import com.pos.be.entity.category.Category;
import com.pos.be.entity.product.Product;
import com.pos.be.entity.product.ProductImage;
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
import com.pos.be.exception.PermissionDeniedException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

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

//    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> saveWithImages(ProductDTO productDTO, MultipartFile[] images) {
//        if (!SecurityUtils.hasPermission(Permissions.PRODUCT_MANAGE)) {
//            throw new PermissionDeniedException("You don't have permission to create products");
//        }
//
//        if (productRepository.findByNameIgnoreCase(productDTO.getName()).isPresent()) {
//            return ResponseEntity.badRequest().body("Product with name '" + productDTO.getName() + "' already exists.");
//        }
//
//        Set<Long> wrongIds = categoryService.existsById(productDTO.getCategoryIds());
//        if (!wrongIds.isEmpty()) {
//            return ResponseEntity.badRequest().body("Category id(s): " + wrongIds + " do not exist.");
//        }
//
//        Set<String> uniqueUrls = new HashSet<>();
//        List<ProductImageDTO> imageDTOs = new ArrayList<>();
//        ProductImageDTO primaryImageDTO = null;
//        List<ProductImageDTO> providedImages = productDTO.getImages();
//
//        // Process image URLs from DTO
//        if (providedImages != null) {
//            for (ProductImageDTO imgDTO : providedImages) {
//                String url = imgDTO.getUrl();
//                if (isValidImageUrl(url) && uniqueUrls.add(url)) {
//                    ProductImageDTO newImageDTO = ProductImageDTO.builder()
//                            .url(url)
//                            .originalFilename(imgDTO.getOriginalFilename()) // copy if exists
//                            .primaryImage(false)
//                            .build();
//                    imageDTOs.add(newImageDTO);
//                    if (imgDTO.isPrimaryImage()) {
//                        newImageDTO.setPrimaryImage(true);
//                        primaryImageDTO = newImageDTO;
//                    }
//                } else {
//                    System.out.println("Invalid URL: " + url);
//                }
//            }
//        }
//
//        // Process uploaded images
//        if (images != null) {
//            for (MultipartFile file : images) {
//                try {
//                    if (file.isEmpty()) continue;
//                    String origFilename = file.getOriginalFilename();
//                    String imageUrl = saveFile(file);
//                    if (isValidUploadedUrl(imageUrl)) {
//                        ProductImageDTO newImageDTO = ProductImageDTO.builder()
//                                .url(imageUrl)
//                                .originalFilename(origFilename)
//                                .primaryImage(false)
//                                .build();
//                        imageDTOs.add(newImageDTO);
//                        if (primaryImageDTO == null) {
//                            primaryImageDTO = newImageDTO; // fallback primary
//                        }
//                    } else {
//                        deleteFile(imageUrl); // cleanup
//                        return ResponseEntity.badRequest()
//                                .body("Invalid image path generated: " + imageUrl);
//                    }
//                } catch (IOException e) {
//                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                            .body("Error saving file: " + file.getOriginalFilename());
//                }
//            }
//        }
//
//        // Enforce single primary image
//        if (primaryImageDTO != null) {
//            for (ProductImageDTO dto : imageDTOs) {
//                dto.setPrimaryImage(dto == primaryImageDTO);
//            }
//        } else if (!imageDTOs.isEmpty()) {
//            imageDTOs.get(0).setPrimaryImage(true);
//        }
//
//        productDTO.setImages(imageDTOs);
//        Product product = convertToEntity(productDTO);
//        Product savedProduct = productRepository.save(product);
//        return ResponseEntity.ok(convertToDTO(savedProduct));
//    }
@PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
public ResponseEntity<?> saveWithImages(ProductDTO dto, MultipartFile[] images) {
    // Check for a duplicate id on save (should be null/new product)
    if (dto.getId() != null) {
        return ResponseEntity.badRequest().body("New product should not have an id.");
    }

    // Build a new Product entity
    Product product = Product.builder()
            .name(dto.getName())
            .description(dto.getDescription())
            .price(dto.getPrice())
            .quantity(dto.getQuantity())
            .unit(dto.getUnit())
            .build();

    // Resolve and assign the categories
    Iterable<Category> iterableCategories = categoryService.findAllByIds(dto.getCategoryIds());
    Set<Category> categories = new HashSet<>();
    iterableCategories.forEach(categories::add);
    product.setCategories(categories);

    // Initialize images container if not already (and to avoid potential null pointer)
    product.setImages(new ArrayList<>());

    // Process file uploads (MultipartFile[]): uploaded files take precedence and are added if they are valid.
    if (images != null && images.length > 0) {
        for (MultipartFile file : images) {
            try {
                String origFilename = file.getOriginalFilename();
                String imageUrl = saveFile(file); // your implementation to save and return URL
                if (imageUrl == null || imageUrl.trim().isEmpty()) {
                    continue;
                }
                // Check if an image with the same original filename or URL already exists
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
                            .primaryImage(false) // initially set primary flag to false
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

    // Process DTO-provided image URLs (non-uploaded ones)
    if (dto.getImages() != null && !dto.getImages().isEmpty()) {
        for (ProductImageDTO imgDto : dto.getImages()) {
            if (imgDto.getUrl() == null || imgDto.getUrl().trim().isEmpty()) {
                continue;
            }
            // If this image is not already present in the product images, then add it
            Optional<ProductImage> existingOpt = product.getImages().stream()
                    .filter(img -> img.getUrl().equals(imgDto.getUrl()))
                    .findFirst();
            if (!existingOpt.isPresent()) {
                ProductImage image = ProductImage.builder()
                        .url(imgDto.getUrl())
                        .originalFilename(imgDto.getOriginalFilename()) // may be null for URL-based images
                        .primaryImage(false)
                        .product(product)
                        .build();
                product.getImages().add(image);
            }
        }
    }

    // FINAL STEP: Determine which image should be marked as primary.
    boolean primarySet = false;
    if (dto.getImages() != null) {
        for (ProductImageDTO imgDto : dto.getImages()) {
            if (!imgDto.isPrimaryImage()) {
                continue;
            }
            // Try to match using URL first, and if not available, then by original filename.
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
                // Clear primary flags from all images and set the matching one as primary
                product.getImages().forEach(img -> img.setPrimaryImage(false));
                matchingImage.get().setPrimaryImage(true);
                primarySet = true;
                break;
            }
        }
    }
    // If no image was marked as primary based on the DTO info, fallback to the first image
    if (!primarySet && !product.getImages().isEmpty()) {
        product.getImages().forEach(img -> img.setPrimaryImage(false));
        product.getImages().get(0).setPrimaryImage(true);
    }

    // Enforce that only one image is marked as primary.
    enforceSinglePrimary(product.getImages());

    // Save the product
    Product savedProduct = productRepository.save(product);
    // Convert to DTO for returning in response (your helper method)
    return ResponseEntity.ok(convertToDTO(savedProduct));
}

    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> updateWithImages(ProductDTO dto, MultipartFile[] images) {
        if (!SecurityUtils.hasPermission(Permissions.PRODUCT_MANAGE)) {
            throw new PermissionDeniedException("You don't have permission to update products");
        }
        Optional<Product> productOpt = productRepository.findById(dto.getId());
        if (!productOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product with id " + dto.getId() + " doesn't exist.");
        }
        Product product = productOpt.get();

        // Enforce immutable product name
        if (!product.getName().equalsIgnoreCase(dto.getName())) {
            return ResponseEntity.badRequest().body("Product name cannot be updated.");
        }

        // Update categories and other fields
        Iterable<Category> iterableCategories = categoryService.findAllByIds(dto.getCategoryIds());
        Set<Category> categories = new HashSet<>();
        iterableCategories.forEach(categories::add);
        product.setCategories(categories);

        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setUnit(dto.getUnit());

        // Remove images that are not present in incoming DTO
        if (dto.getImages() != null) {
            // Collect URLs from the DTO images. Note that for uploaded files the url is the saved URL.
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

        // Process file uploads. Use original filename for matching with DTO entries.
        if (images != null && images.length > 0) {
            for (MultipartFile file : images) {
                try {
                    String origFilename = file.getOriginalFilename();
                    String imageUrl = saveFile(file);
                    if (imageUrl == null || imageUrl.trim().isEmpty()) continue;
                    // Check if an image with the same original filename already exists in the product
                    Optional<ProductImage> existingOpt = product.getImages().stream()
                            .filter(img -> {
                                // if the image has an originalFilename recorded, match it;
                                // otherwise, fall back to the URL match.
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
                                .primaryImage(false) // initially false
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

        // Process DTO-provided image URLs (non-uploaded ones)
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (ProductImageDTO imgDto : dto.getImages()) {
                if (imgDto.getUrl() == null || imgDto.getUrl().trim().isEmpty()) continue;
                // If this image is not already in our product images, add it.
                Optional<ProductImage> existingOpt = product.getImages().stream()
                        .filter(img -> img.getUrl().equals(imgDto.getUrl()))
                        .findFirst();
                if (!existingOpt.isPresent()) {
                    product.getImages().add(ProductImage.builder()
                            .url(imgDto.getUrl())
                            .primaryImage(false)
                            .originalFilename(imgDto.getOriginalFilename()) // may be null for URL-based images
                            .product(product)
                            .build());
                }
            }
        }

        // FINAL STEP: Set primary flag based on DTO info using the original filename if possible.
        // For each DTO image marked as primary, match it in our product's images.
        // If none are flagged, fallback to first image.
        boolean primarySet = false;
        if (dto.getImages() != null) {
            for (ProductImageDTO imgDTO : dto.getImages()) {
                if (!imgDTO.isPrimaryImage()) continue;
                // Try to find by URL first, then by originalFilename
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
                    // Reset all to false and mark the matched image true
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

        // Enforce only one primary image.
        enforceSinglePrimary(product.getImages());
        Product updatedProduct = productRepository.save(product);
        return ResponseEntity.ok(convertToDTO(updatedProduct));
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

    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> get(Long id) {
        if (!SecurityUtils.hasPermission(Permissions.PRODUCT_VIEW)) {
            throw new PermissionDeniedException("You don't have permission to view products");
        }
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            return ResponseEntity.ok(convertToDTO(productOpt.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Product with id " + id + " doesn't exist.");
    }

    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Page<ProductDTO> getProducts(String name, Pageable pageable) {
        if (!SecurityUtils.hasPermission(Permissions.PRODUCT_VIEW)) {
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

    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> getDetailedProduct(Long id) {
        if (!SecurityUtils.hasPermission(Permissions.PRODUCT_VIEW)) {
            throw new PermissionDeniedException("You don't have permission to view product details");
        }
        Object details = productRepository.getProductDetailsById(id);
        if (details == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product with id " + id + " not found.");
        }
        return ResponseEntity.ok(details);
    }

    @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> delete(Long id) {
        if (!SecurityUtils.hasPermission(Permissions.PRODUCT_MANAGE)) {
            throw new PermissionDeniedException("You don't have permission to delete products");
        }
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
            if (file.exists()) {
                if (!file.delete()) {
                    System.err.println("Failed to delete file: " + filePath);
                }
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
                            .originalFilename(img.getOriginalFilename()) // new field
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

    private Product convertToEntity(ProductDTO dto) {
        Iterable<Category> iterableCategories = categoryService.findAllByIds(dto.getCategoryIds());
        Set<Category> categorySet = new HashSet<>();
        iterableCategories.forEach(categorySet::add);
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .unit(dto.getUnit())
                .categories(categorySet)
                .build();
        if (dto.getImages() != null) {
            List<ProductImage> imageList = dto.getImages().stream()
                    .map(imgDto -> ProductImage.builder()
                            .url(imgDto.getUrl())
                            .originalFilename(imgDto.getOriginalFilename()) // new field
                            .primaryImage(imgDto.isPrimaryImage())
                            .product(product)
                            .build())
                    .collect(Collectors.toList());
            product.setImages(imageList);
        }
        return product;
    }
        @PreAuthorize("hasAuthority('" + Permissions.PRODUCT_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Page<ProductDTO> getProductsByCategory(Long categoryId, String query, Pageable pageable) {
        // Additional service-level permission check
        if (!SecurityUtils.hasPermission(Permissions.PRODUCT_VIEW)) {
            throw new PermissionDeniedException("You don't have permission to view products");
        }
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

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

}
