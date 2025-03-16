//package com.pos.be.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FileStorageConfig {
//
//    @Value("${file.upload.absolute-path}")
//    private String absolutePath;
//
//    @Value("${file.upload.url-path}")
//    private String urlPath;
//
//    @Bean("absoluteUploadPath")
//    public String absoluteUploadPath() {
//        return absolutePath;
//    }
//
//    @Bean("uploadUrl")
//    public String uploadUrl() {
//        return urlPath;
//    }
//}