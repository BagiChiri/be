package com.pos.be.service.util;

import okhttp3.*;
import java.io.*;
import java.nio.file.*;
import java.util.Objects;

public class ImageProcessor {
    private static final String DIRECTORY_PATH = "./images"; // Input directory
    private static final String OUTPUT_DIR = "./output"; // Output directory
    private static final String REMOVE_BG_API_URL = "https://api.developer.pixelcut.ai/v1/remove-background";
    //    private static final String API_KEY = "sk_92438ae356b3481b8d1c4813c5f03ed0"; // Replace with your actual API key
//    private static final String API_KEY = "sk_d0b379568cc445f18ed9097734496dc3"; //kotilic115@egvoo.com // Replace with your actual API key
    private static final String API_KEY = "sk_9490617e73734480ae7256f23839eb69"; // deboki6697@egvoo.com // Replace with your actual API key

    private static final OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) {
        try {
            Files.createDirectories(Paths.get(OUTPUT_DIR)); // Ensure output directory exists
            removeImagesBg();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeImagesBg() {
        File dir = new File(DIRECTORY_PATH);
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg"));

        if (files == null || files.length == 0) {
            System.out.println("No image files found in " + DIRECTORY_PATH);
            return;
        }

        for (File file : files) {
            try {
                System.out.println("Processing: " + file.getName());
                byte[] responseBytes = sendPostRequest(file);

                if (responseBytes != null) {
                    saveFile(responseBytes, file);
                    deleteOriginalFile(file);
                }
            } catch (Exception e) {
                System.err.println("Error processing " + file.getName() + ": " + e.getMessage());
            }
        }

        System.out.println("All images processed!");
    }

    private static byte[] sendPostRequest(File imageFile) throws IOException {
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", imageFile.getName(),
                        RequestBody.create(imageFile, MediaType.parse("image/png")))
                .addFormDataPart("format", "png")
                .build();

        Request request = new Request.Builder()
                .url(REMOVE_BG_API_URL)
                .post(body)
                .addHeader("X-API-KEY", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Unexpected response: " + response);
            }
            return Objects.requireNonNull(response.body()).bytes();
        }
    }

    private static void saveFile(byte[] data, File originalFile) throws IOException {
        String fileName = originalFile.getName().replace(".jpg", "").replace(".png", "") + "_bg_removed.png";
        Path outputPath = Paths.get(OUTPUT_DIR, fileName);
        Files.write(outputPath, data);
        System.out.println("Saved: " + outputPath);
    }

    private static void deleteOriginalFile(File file) {
        if (file.delete()) {
            System.out.println("Deleted original file: " + file.getName());
        } else {
            System.err.println("Failed to delete: " + file.getName());
        }
    }
}
