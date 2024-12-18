package com.pos.be.service;

import com.pos.be.entity.category.Category;
import com.pos.be.entity.product.CustomOptions;
import com.pos.be.entity.product.Product;
import com.pos.be.repository.category.CategoryRepository;
import com.pos.be.repository.product.CustomOptionsRepository;
import com.pos.be.repository.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

//@Service
public class DatabaseSeederService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CustomOptionsRepository customOptionsRepository;

    public DatabaseSeederService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            CustomOptionsRepository customOptionsRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.customOptionsRepository = customOptionsRepository;
    }

    public void seedDatabase() {
        seedCategories();
        seedProducts();
        seedProductCategoryRelationships();
        seedCustomOptions();
    }

    private void seedCategories() {
        List<Category> categories = new ArrayList<>();
        for (int i = 1; i <= 12000; i++) {
            categories.add(new Category(null, "Category " + i));
        }
        categoryRepository.saveAll(categories);
        System.out.println("12,000 categories inserted.");
    }

    private void seedProducts() {
        Random random = new Random();
        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= 12000; i++) {
            products.add(
                    Product.builder()
                            .name("Product " + i)
                            .description("Description for Product " + i)
                            .price(random.nextDouble() * 100)
                            .quantity(random.nextInt(50) + 1)
                            .build()
            );
        }
        productRepository.saveAll(products);
        System.out.println("12,000 products inserted.");
    }

    private void seedProductCategoryRelationships() {
        Iterable<Product> p = productRepository.findAll();
        Iterable<Category> c = categoryRepository.findAll();
        List<Product> products = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        p.forEach(product -> products.add(product));
        c.forEach(category -> categories.add(category));

        Random random = new Random();
        for (Product product : products) {
            int numCategories = random.nextInt(5) + 1; // Each product gets 1-5 categories
            Set<Category> assignedCategories = new HashSet<>();

            for (int i = 0; i < numCategories; i++) {
                Category randomCategory = categories.get(random.nextInt(categories.size()));
                assignedCategories.add(randomCategory);
            }
            product.setCategories(assignedCategories);
        }

        productRepository.saveAll(products);
        System.out.println("Product-category relationships seeded.");
    }

    private void seedCustomOptions() {
        Random random = new Random();
        Iterable<Product> p = productRepository.findAll();
        List<Product> products = new ArrayList<>();
        p.forEach(product -> products.add(product));
        List<CustomOptions> customOptions = new ArrayList<>();

        for (int i = 1; i <= 12000; i++) {
            Product randomProduct = products.get(random.nextInt(products.size()));
            List<String> values = List.of("Value A", "Value B", "Value C");

            customOptions.add(
                    CustomOptions.builder()
                            .optionLabel("Option Label " + i)
                            .optionType(random.nextBoolean() ? "Type A" : "Type B")
                            .optionValue(values)
                            .product(randomProduct)
                            .build()
            );
        }

        customOptionsRepository.saveAll(customOptions);
        System.out.println("12,000 custom options inserted.");
    }
}
