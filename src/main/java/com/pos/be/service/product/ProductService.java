package com.pos.be.service.product;

import com.pos.be.dto.product.ProductDTO;
import com.pos.be.dto.product.ProductImageDTO;
import com.pos.be.entity.category.Category;
import com.pos.be.entity.product.Product;
import com.pos.be.entity.product.ProductImage;
import com.pos.be.repository.category.CategoryRepository;
import com.pos.be.repository.order.OrderRepository;
import com.pos.be.repository.product.ProductRepository;
import com.pos.be.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final OrderRepository orderRepository;

    // Define the upload directory (adjust as necessary or externalize to properties)
//    private final String absoluteUploadPath = "C:/uploads/products/";
    // Define paths with proper separator handling
    @Value("${file.upload.absolute-path}")
    private String absoluteUploadPath;// = "C:" + File.separator + "uploads" + File.separator + "products" + File.separator;
    @Value("${file.upload.url-path}")
    private String uploadDir;// = "/uploads/products/";

    /**
     * Creates a new product with optional image file uploads.
     */
    public ResponseEntity<?> saveWithImages(ProductDTO productDTO, MultipartFile[] images) {
        if (productRepository.findByNameIgnoreCase(productDTO.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Product with name '" + productDTO.getName() + "' already exists.");
        }
        Set<Long> wrongIds = categoryService.existsById(productDTO.getCategoryIds());
        if (!wrongIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Category id(s): " + wrongIds + " do not exist.");
        }

        Set<String> uniqueUrls = new HashSet<>();
        Set<String> uniqueImages = new HashSet<>();
        List<ProductImageDTO> imageDTOs = new ArrayList<>();
        ProductImageDTO primaryImageDTO = null;

        // Process existing URLs from productDTO
        List<ProductImageDTO> providedImages = productDTO.getImages();
        if (providedImages != null) {
            for (ProductImageDTO imgDTO : providedImages) {
                String url = imgDTO.getUrl();
                if (isValidImageUrl(url) && uniqueUrls.add(url)) {
                    ProductImageDTO newImageDTO = ProductImageDTO.builder()
                            .url(url)
                            .primaryImage(false)
                            .build();
                    imageDTOs.add(newImageDTO);
                    if (imgDTO.isPrimaryImage() && primaryImageDTO == null) {
                        primaryImageDTO = newImageDTO;
                    }
                } else {
                    System.out.println("invalid url: " + url);
                }
            }
        }

        // Process uploaded image files
        if (images != null) {
            for (MultipartFile file : images) {
                try {
                    if (file.isEmpty()) continue;

                    String imageUrl = saveFile(file);
                    // Changed to use isValidUploadedUrl instead of isValidImageUrl
                    if (isValidUploadedUrl(imageUrl)) {
                        if (uniqueImages.add(imageUrl)) {
                            ProductImageDTO newImageDTO = ProductImageDTO.builder()
                                    .url(imageUrl)
                                    .primaryImage(false)
                                    .build();
                            imageDTOs.add(newImageDTO);
                            if (primaryImageDTO == null) {
                                primaryImageDTO = newImageDTO;
                            }
                        }
                    } else {
                        deleteFile(imageUrl); // Clean up invalid file
                        return ResponseEntity.badRequest()
                                .body("Invalid image path generated: " + imageUrl);
                    }
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error saving file: " + file.getOriginalFilename());
                }
            }
        }


        // Ensure only one primary image is set
        if (primaryImageDTO != null) {
            primaryImageDTO.setPrimaryImage(true);
        } else if (!imageDTOs.isEmpty()) {
            imageDTOs.get(0).setPrimaryImage(true);
        }

        productDTO.setImages(imageDTOs);
        Product product = convertToEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.ok(convertToDTO(savedProduct));
    }

    private boolean isValidImageUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }

        // Check for web URL
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return isValidWebUrl(url);
        }

        // Check for uploaded file path
        return url.startsWith("/uploads/products/")
                && !url.contains("..")
                && url.length() > "/uploads/products/".length();
    }

    private boolean isValidWebUrl(String url) {
        // Basic URL validation for web URLs
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    // Additional helper method for uploaded URLs
    private boolean isValidUploadedUrl(String url) {
        return url.startsWith("/uploads/products/")
                && !url.contains("..")
                && url.length() > "/uploads/products/".length();
    }
    /**
     * Updates an existing product with optional new image file uploads.
     */
    public ResponseEntity<?> updateWithImages(ProductDTO dto, MultipartFile[] images) {
        Optional<Product> productOpt = productRepository.findById(dto.getId());
        if (!productOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product with id " + dto.getId() + " doesn't exist.");
        }
        Product product = productOpt.get();

        // Enforce that product name remains immutable.
        if (!product.getName().equalsIgnoreCase(dto.getName())) {
            return ResponseEntity.badRequest().body("Product name cannot be updated.");
        }

        // Update categories.
        Iterable<Category> iterableCategories = categoryService.findAllByIds(dto.getCategoryIds());
        Set<Category> categories = new HashSet<>();
        iterableCategories.forEach(categories::add);
        product.setCategories(categories);

        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setUnit(dto.getUnit());

        // Ensure product images collection is not null.
        if (product.getImages() == null) {
            product.setImages(new ArrayList<>());
        }

        // Process file uploads if provided.
        if (images != null && images.length > 0) {
            Set<String> newUrls = new HashSet<>();
            List<ProductImage> newImages = new ArrayList<>();

            for (MultipartFile file : images) {
                try {
                    if (file.isEmpty()) continue;

                    String imageUrl = saveFile(file);
                    if (!isValidUploadedUrl(imageUrl)) {
                        deleteFile(imageUrl); // Clean up invalid upload
                        return ResponseEntity.badRequest()
                                .body("Invalid uploaded image path: " + imageUrl);
                    }

                    // Check duplicates in existing and new images
                    boolean isDuplicate = product.getImages().stream()
                            .anyMatch(img -> img.getUrl().equalsIgnoreCase(imageUrl))
                            || newUrls.stream().anyMatch(u -> u.equalsIgnoreCase(imageUrl));

                    if (!isDuplicate) {
                        ProductImage newImage = new ProductImage();
                        newImage.setUrl(imageUrl);
                        newImage.setPrimaryImage(false);
                        newImage.setProduct(product);
                        newImages.add(newImage);
                        newUrls.add(imageUrl);
                    }
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error saving file: " + file.getOriginalFilename());
                }
            }
            product.getImages().addAll(newImages);
        }

        // Process DTO-provided image URLs if provided.
        if (dto.getImages() != null) {
            Set<String> dtoUrls = new HashSet<>();

            for (ProductImageDTO imgDto : dto.getImages()) {
                String url = imgDto.getUrl();

                if (!isValidImageUrl(url)) {
                    return ResponseEntity.badRequest()
                            .body("Invalid image URL: " + url);
                }

                // Case-insensitive duplicate check
                String lowerUrl = url.toLowerCase();
                if (!dtoUrls.add(lowerUrl)) {
                    return ResponseEntity.badRequest()
                            .body("Duplicate URL in request: " + url);
                }

                Optional<ProductImage> existing = product.getImages().stream()
                        .filter(img -> img.getUrl().equalsIgnoreCase(url))
                        .findFirst();

                if (existing.isPresent()) {
                    // Update existing image (both web and upload URLs)
                    existing.get().setPrimaryImage(imgDto.isPrimaryImage());
                } else {
                    // Add new image (validate uploaded URLs don't mix with web URLs)
                    if (url.startsWith("/uploads/products/") &&
                            product.getImages().stream().anyMatch(img -> img.getUrl().equalsIgnoreCase(url))
                    ) {
                        return ResponseEntity.badRequest()
                                .body("Uploaded image URL conflicts with existing: " + url);
                    }

                    product.getImages().add(ProductImage.builder()
                            .url(url)
                            .primaryImage(imgDto.isPrimaryImage())
                            .product(product)
                            .build());
                }
            }
        }


        // Enforce only one image is marked as primary.
        enforceSinglePrimary(product.getImages());

        Product updatedProduct = productRepository.save(product);
        return ResponseEntity.ok(convertToDTO(updatedProduct));
    }

    /**
     * Ensures that only one image in the list is marked as primary.
     * If more than one is marked primary, only the first encountered remains primary.
     * If none are marked, the first image is set as primary.
     */
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


    /**
     * Retrieves a product by its id.
     */
    public ResponseEntity<?> get(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            return ResponseEntity.ok(convertToDTO(productOpt.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Product with id " + id + " doesn't exist.");
    }

    /**
     * Retrieves products with optional name filtering.
     */
    public Page<ProductDTO> getProducts(String name, Pageable pageable) {
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

    /**
     * Retrieves products by a specific category id.
     */
    public Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable) {
        Page<Product> page = productRepository.findByCategories_Id(categoryId, pageable);
        List<ProductDTO> dtos = page.getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    /**
     * Retrieves detailed product information using a native query.
     */
    public ResponseEntity<?> getDetailedProduct(Long id) {
        Object details = productRepository.getProductDetailsById(id);
        if (details == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product with id " + id + " not found.");
        }
        return ResponseEntity.ok(details);
    }

    /**
     * Deletes a product and its associated images.
     */
    public ResponseEntity<?> delete(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (!productOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product with id " + id + " doesn't exist.");
        }
        if (orderRepository.existsOrderItemByProductId(id)) {
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

    // --- Utility Methods ---

    /**
     * Saves an uploaded file to the designated directory and returns its URL/path.
     */
    private String saveFile(MultipartFile file) throws IOException {
        // Validate upload directory configuration
        if (absoluteUploadPath == null || absoluteUploadPath.isBlank()) {
            throw new IOException("Upload directory is not configured properly");
        }

        File uploadPath = new File(absoluteUploadPath);
        if (!uploadPath.exists() && !uploadPath.mkdirs()) {
            throw new IOException("Could not create directory: " + absoluteUploadPath);
        }

        // Validate filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IOException("Invalid file name");
        }

        // Sanitize filename
        String cleanFilename = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        String fileName = System.currentTimeMillis() + "_" + cleanFilename;

        // Create destination path
        File dest = new File(uploadPath, fileName);

        // Save file
        file.transferTo(dest);

        // Return web-accessible URL path
        return uploadDir + fileName; // Adjust this to match your static resource mapping
    }


    /**
     * Deletes a file given its path.
     */
    private void deleteFile(String fileUrl) {
        try {
            // Convert URL path to filesystem path
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

    /**
     * Converts a Product entity to a ProductDTO.
     */
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

    /**
     * Converts a ProductDTO to a Product entity.
     */
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
                            .primaryImage(imgDto.isPrimaryImage())
                            .product(product)
                            .build())
                    .collect(Collectors.toList());
            product.setImages(imageList);
        }
        return product;
    }
}
