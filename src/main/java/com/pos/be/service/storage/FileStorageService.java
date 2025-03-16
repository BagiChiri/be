package com.pos.be.service.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path rootLocation = Paths.get("C:/uploads/products");

    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage!");
        }
    }

    public String store(MultipartFile file) {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename));
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }
    }

    public void delete(String filename) {
        try {
            Files.deleteIfExists(rootLocation.resolve(filename));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file " + filename, e);
        }
    }
}