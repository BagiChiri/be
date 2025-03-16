//package com.pos.be.controller.product;
//
//import com.pos.be.dto.product.ProductImageDTO;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/products/images")
//public class ProductImageController {
//
//    @Value("${app.upload.dir}")
//    private String uploadDir;
//
//    @PostMapping("/upload")
//    public ResponseEntity<?> uploadImage(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam(value = "primary", defaultValue = "false") boolean primary) {
//
//        try {
//            if (file.isEmpty()) return badRequest("File is empty");
//            if (!file.getContentType().startsWith("image/")) return badRequest("Only images allowed");
//
//            Path uploadPath = Paths.get(uploadDir);
//            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
//
//            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
//            String filename = UUID.randomUUID() + "." + extension;
//            Files.copy(file.getInputStream(), uploadPath.resolve(filename));
//
//            return ResponseEntity.ok(new ProductImageDTO("/uploads/" + filename, primary));
//
//        } catch (IOException ex) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Upload failed: " + ex.getMessage());
//        }
//    }
//
//    private ResponseEntity<?> badRequest(String message) {
//        return ResponseEntity.badRequest().body(message);
//    }
//}